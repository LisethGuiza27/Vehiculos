package com.garaje.controller;

import com.garaje.facade.BusinessException;
import com.garaje.facade.vehiculoFacade;
import com.garaje.model.vehiculo;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador HTTP para Vehículos.
 * GET: list, edit, (opcional: delete)
 * POST: create, update, delete
 */
@WebServlet("/vehiculos")
public class VehiculoServlet extends HttpServlet {

    @EJB
    private vehiculoFacade facade;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "list";
        try {
            switch (action) {
                case "list":
                default:
                    listar(req, resp);
                    break;

                case "new":
                    // Si usas un JSP separado para "nuevo", sino omite:
                    req.setAttribute("vehiculo", new vehiculo());
                    listar(req, resp); // Mantén la misma vista index.jsp con el formulario de crear
                    break;

                case "edit":
                    editar(req, resp); // Carga el registro en editVehiculo y reenvía a index.jsp
                    break;

                case "delete": // opcional por si tienes enlaces GET (recomendado usar POST)
                    eliminar(req, resp);
                    break;
            }
        } catch (Exception e) {
            req.setAttribute("error", mensaje(e));
            try { listar(req, resp); } catch (Exception ex) { throw new ServletException(ex); }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                vehiculo v = bind(req);
                facade.agregar(v);
                req.setAttribute("ok", "Vehículo agregado.");
                listar(req, resp);

            } else if ("update".equals(action)) {
                vehiculo v = bind(req);
                // id obligatorio para actualizar
                v.setId(Integer.parseInt(req.getParameter("id")));
                facade.actualizar(v);
                req.setAttribute("ok", "Vehículo actualizado.");
                listar(req, resp);

            } else if ("delete".equals(action)) {
                eliminar(req, resp);

            } else {
                resp.sendRedirect(req.getContextPath() + "/vehiculos?action=list");
            }
        } catch (Exception e) {
            req.setAttribute("error", mensaje(e));
            try { listar(req, resp); } catch (Exception ex) { throw new ServletException(ex); }
        }
    }

    /** Carga listado y reenvía a la vista principal. */
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        List<vehiculo> lista = facade.listar();
        req.setAttribute("lista", lista);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    /** Carga el vehículo a editar y reenvía a index.jsp (la JSP muestra el form si existe editVehiculo). */
    private void editar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException {
        int id = Integer.parseInt(req.getParameter("id"));
        vehiculo v = facade.buscarPorId(id);
        if (v == null) {
            req.setAttribute("error", "No existe el vehículo (id=" + id + ").");
        } else {
            req.setAttribute("editVehiculo", v);
        }
        listar(req, resp);
    }

    /** Ejecuta eliminación por id con manejo de negocio. */
    private void eliminar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            facade.eliminar(id);
            req.setAttribute("ok", "Vehículo eliminado.");
        } catch (Exception e) {
            req.setAttribute("error", mensaje(e));
        }
        try { listar(req, resp); } catch (Exception ex) { throw new ServletException(ex); }
    }

    /** Bindea parámetros del formulario a la entidad. */
    private vehiculo bind(HttpServletRequest req) {
        vehiculo v = new vehiculo();
        v.setPlaca(req.getParameter("placa"));
        v.setMarca(req.getParameter("marca"));
        v.setModelo(req.getParameter("modelo")); // usado como año
        v.setColor(req.getParameter("color"));
        v.setPropietario(req.getParameter("propietario"));
        return v;
    }

    /** Devuelve mensaje apto para el usuario. */
    private String mensaje(Exception e) {
        if (e instanceof BusinessException) return e.getMessage();
        if (e.getCause() instanceof BusinessException) return e.getCause().getMessage();
        if (e instanceof SQLException) return "Error de base de datos.";
        return "Ha ocurrido un error. " + e.getMessage();
    }
}
