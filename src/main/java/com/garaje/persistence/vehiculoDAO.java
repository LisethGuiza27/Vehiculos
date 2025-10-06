package com.garaje.persistence;

import com.garaje.model.vehiculo;
import java.sql.*;
import java.util.*;

/**
 * DAO de vehículos con operaciones CRUD y utilidades (existePlaca).
 */
public class vehiculoDAO {

    /** Conexión JDBC provista por la capa superior*/
    private final Connection con;

    /**
     * Crea el DAO con una conexión abierta.
     */
    public vehiculoDAO(Connection con) { this.con = con; }

    /**
     * Obtiene todos los vehículos ordenados descendentemente por id.
     *
     ejecuta un SELECT * FROM vehiculos, itera el ResultSet y mapea cada fila a la entidad {@link vehiculo}.
     * @return lista de vehículos
     * @throws SQLException si ocurre un error al consultar
     */
    public List<vehiculo> listar() throws SQLException {
        List<vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos ORDER BY id DESC";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                vehiculo v = new vehiculo(
                    rs.getInt("id"),
                    rs.getString("placa"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getString("color"),
                    rs.getString("propietario")
                );
                lista.add(v);
            }
        }
        return lista;
    }

    /**
     * Busca un vehículo por su ID.
     ejecuta un SELECT con filtro por id usando PreparedStatement y devuelve la entidad o null si no existe.
     * @param id identificador del vehículo
     * @return vehículo encontrado
     * @throws SQLException si ocurre un error al consultar
     */
    public vehiculo buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM vehiculos WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new vehiculo(
                        rs.getInt("id"),
                        rs.getString("placa"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("color"),
                        rs.getString("propietario")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Verifica si ya existe una placa registrada.
     ejecuta SELECT COUNT(*) filtrando por placa y devuelve true si el conteo es mayor que 0.
     * @param placa texto de la placa a verificar
     * @return {@code true} si ya existe
     * @throws SQLException si ocurre un error al consultar
     */
    public boolean existePlaca(String placa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vehiculos WHERE placa=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Inserta un nuevo vehículo en la base de datos.
     ejecuta un INSERT parametrizado con los campos placa, marca, modelo, color y propietario de la entidad.
     */
    public void agregar(vehiculo v) throws SQLException {
        String sql = "INSERT INTO vehiculos (placa, marca, modelo, color, propietario) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setString(4, v.getColor());
            ps.setString(5, v.getPropietario());
            ps.executeUpdate();
        }
    }

    /**
     * Actualiza los datos de un vehículo existente (match por id).
     ejecuta un UPDATE parametrizado de todos los campos editables y filtra por el id de la entidad.
     *
     * @param v entidad con nuevos valores (debe traer id)
     * @throws SQLException si ocurre un error al actualizar
     */
    public void actualizar(vehiculo v) throws SQLException {
        String sql = "UPDATE vehiculos SET placa=?, marca=?, modelo=?, color=?, propietario=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setString(4, v.getColor());
            ps.setString(5, v.getPropietario());
            ps.setInt(6, v.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un vehículo por su ID.
     ejecuta un DELETE parametrizado filtrando por id.
     * @param id identificador del vehículo a eliminar
     * @throws SQLException si ocurre un error al eliminar
     */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM vehiculos WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

