/**
 * Fachada con reglas de negocio: placa única, propietario>=5,
 * marca/modelo/placa>=3, color permitido (Rojo/Blanco/Negro/Azul/Gris),
 * antigüedad <= 20 años (modelo como año), anti-SQLi,
 * no eliminar si propietario=Administrador, notificación si marca=Ferrari.
 * Usa DataSource JNDI jdbc/myPool (GlassFish).
 */

package com.garaje.facade;

import com.garaje.model.vehiculo;
import com.garaje.persistence.vehiculoDAO;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;
import java.util.*;

/**
 * Fachada (capa de negocio) para gestionar vehículos.
 * Expone operaciones CRUD y valida reglas de negocio antes de llamar al DAO.
 */
@Stateless
public class vehiculoFacade {

    /*
     * DataSource inyectado desde el contenedor de aplicaciones.
     */
    @Resource(lookup="jdbc/myPool")
    private DataSource ds;

    /**
     * Conjunto blanco de colores permitidos para el vehículo.
     * Se usa en la validación básica.
     */
    private static final Set<String> COLORES =
        new HashSet<>(Arrays.asList("Rojo","Blanco","Negro","Azul","Gris"));

    /**
     * Lista todos los vehículos.
     * @return lista completa de vehículos
     * @throws SQLException si ocurre un error de acceso a datos
     */
    public List<vehiculo> listar() throws SQLException {
        try (Connection con = ds.getConnection()) {
            return new vehiculoDAO(con).listar();
        }
    }

    /**
     * Busca un vehículo por su identificador, abre conexión y llama a vehiculoDAO.buscarPorId(id).
     * @param id identificador del vehículo
     * @return el vehículo encontrado si no existe
     * @throws SQLException si ocurre un error de acceso a datos
     */
    public vehiculo buscarPorId(int id) throws SQLException {
        try (Connection con = ds.getConnection()) {
            return new vehiculoDAO(con).buscarPorId(id);
        }
    }

    /**
     * Crea (agrega) un nuevo vehículo.
     * 1) Ejecuta validaciones de negocio para creación (placa no duplicada, campos, año, etc.).
     * 2) Inserta vía DAO.
     * 3) Lanza una notificación en consola si la marca es "Ferrari".
     * @throws BusinessException si alguna regla de negocio no se cumple
     */
    public void agregar(vehiculo v) throws SQLException, BusinessException {
        validarParaCrear(v);
        try (Connection con = ds.getConnection()) {
            new vehiculoDAO(con).agregar(v);
            if ("ferrari".equalsIgnoreCase(v.getMarca())) {
                System.out.println("[NOTIFICACION] ¡Ferrari registrado! Placa: " + v.getPlaca());
            }
        }
    }

    /**
     * Actualiza un vehículo existente.

     * 1) Valida los campos básicos (formato, año, color, sanitización).
     * 2) Verifica que el vehículo exista por ID.
     * 3) Si la placa cambió, valida que no esté duplicada en BD.
     * 4) Ejecuta la actualización vía DAO.
     */
    public void actualizar(vehiculo v) throws SQLException, BusinessException {
        validarBasico(v);
        try (Connection con = ds.getConnection()) {
            vehiculoDAO dao = new vehiculoDAO(con);
            vehiculo actual = dao.buscarPorId(v.getId());
            if (actual == null) throw new BusinessException("No existe el vehículo (id=" + v.getId() + ").");
            if (!actual.getPlaca().equalsIgnoreCase(v.getPlaca()) && dao.existePlaca(v.getPlaca()))
                throw new BusinessException("La placa ya existe (no se permite duplicado).");
            dao.actualizar(v);
        }
    }

    /**
     * Elimina un vehículo por su ID.

     * 1) Busca el vehículo; si no existe, no hace nada (idempotente).
     * 2) Impide eliminar si el propietario es "Administrador".
     * 3) Si pasa las reglas, elimina vía DAO.
     */
    public void eliminar(int id) throws SQLException, BusinessException {
        try (Connection con = ds.getConnection()) {
            vehiculoDAO dao = new vehiculoDAO(con);
            vehiculo v = dao.buscarPorId(id);
            if (v == null) return;
            if ("Administrador".equalsIgnoreCase(v.getPropietario()))
                throw new BusinessException("Prohibido eliminar propietario 'Administrador'.");
            dao.eliminar(id);
        }
    }

    /**
     * Valida reglas previas a la creación.

     * 1) Llama a {@link #validarBasico(vehiculo)}.
     * 2) Verifica en BD que la placa no exista.

     * @throws BusinessException si la placa ya está registrada o falla una validación básica
     */
    private void validarParaCrear(vehiculo v) throws SQLException, BusinessException {
        validarBasico(v);
        try (Connection con = ds.getConnection()) {
            if (new vehiculoDAO(con).existePlaca(v.getPlaca()))
                throw new BusinessException("Placa ya registrada.");
        }
    }

    /**
     * Valida reglas comunes a crear/actualizar.
 
     *   Longitudes mínimas de placa, marca, modelo (año) y propietario.
     *   Color dentro de la lista blanca {@link #COLORES} (si viene informado).
     *   Año del modelo: no mayor a +1 del año actual y no menor a (actual - 20).
     *   Entrada “sucia” o peligrosa: bloquea secuencias típicas de inyección SQL.

     * @throws BusinessException si alguna regla de negocio no se cumple
     */
    private void validarBasico(vehiculo v) throws BusinessException {
        if (v.getPlaca()==null || v.getPlaca().trim().length()<3)
            throw new BusinessException("Placa mínima de 3 caracteres.");
        if (v.getMarca()==null || v.getMarca().trim().length()<3)
            throw new BusinessException("Marca mínima de 3 caracteres.");
        if (v.getModelo()==null || v.getModelo().trim().length()<3)
            throw new BusinessException("Modelo (año) mínimo de 3 caracteres.");
        if (v.getPropietario()==null || v.getPropietario().trim().length()<5)
            throw new BusinessException("Propietario mínimo de 5 caracteres.");

        if (v.getColor()!=null && !v.getColor().trim().isEmpty()) {
            boolean ok = COLORES.stream().anyMatch(c -> c.equalsIgnoreCase(v.getColor().trim()));
            if (!ok) throw new BusinessException("Color permitido: " + String.join(", ", COLORES));
        }

        int currentYear = Year.now().getValue();
        try {
            int anio = Integer.parseInt(v.getModelo().trim());
            if (anio < currentYear - 20) throw new BusinessException("Vehículo > 20 años (muy antiguo).");
            if (anio > currentYear + 1) throw new BusinessException("El modelo no puede ser del futuro.");
        } catch (NumberFormatException ex) {
            throw new BusinessException("Modelo debe ser un año numérico (p.ej. 2019).");
        }

        // (lista de patrones prohibidos en todos los campos relevantes)
        String all = String.join("|",
                safe(v.getPlaca()), safe(v.getMarca()), safe(v.getModelo()),
                safe(v.getColor()), safe(v.getPropietario())).toLowerCase();
        String[] bad = {";", "--", "/*", "*/", " drop ", " insert ", " delete ", " update ", " select ", " or 1=1"};
        for (String t : bad) if (all.contains(t)) throw new BusinessException("Entrada no permitida.");
    }

    private String safe(String s) { return s==null ? "" : " "+s.trim()+" "; }
}
