package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.dao.DireccionDAO;
import itson.ecommercewebaplication.models.Direccion;
import itson.ecommercewebaplication.models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Controlador del panel de cuenta del cliente. El GET carga el perfil y las
 * direcciones (la sección "Mis pedidos" la llena cuenta.js por Fetch). El
 * POST atiende todas las acciones del panel: actualizar perfil, cambiar
 * contraseña y el ABC de direcciones (agregar, editar, eliminar y marcar
 * principal). En cada operación sobre una dirección se verifica que sea del
 * propio usuario antes de tocarla.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "CuentaServlet", urlPatterns = {"/app/cuenta"})
public class CuentaServlet extends HttpServlet {

    private UsuarioBO usuarioBO;
    private DireccionDAO direccionDAO;

    @Override
    public void init() throws ServletException {
        usuarioBO = new UsuarioBO();
        direccionDAO = new DireccionDAO();
    }

    /**
     * doGet sirve la página de cuenta. La sección "Mis pedidos" se llena
     * vía Fetch desde cuenta.js (GET /api/pedidos/usuario/{id}).
     * Aquí solo cargamos direcciones y datos de perfil.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        Usuario usuario = session != null ? (Usuario) session.getAttribute("clienteLogueado") : null;

        if (usuario == null) {
            res.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        req.setAttribute("usuario", usuario);
        req.setAttribute("direcciones", direccionDAO.obtenerPorUsuario(usuario.getId()));

        if (req.getParameter("seccion") != null) {
            req.setAttribute("seccionActiva", req.getParameter("seccion"));
        }

        req.getRequestDispatcher("/views/aplication/cuenta.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        Usuario usuario = session != null ? (Usuario) session.getAttribute("clienteLogueado") : null;

        if (usuario == null) {
            res.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String accion = req.getParameter("accion");

        if ("actualizarPerfil".equals(accion)) {
            handleActualizarPerfil(req, res, session, usuario);
        } else if ("cambiarContrasena".equals(accion)) {
            handleCambiarContrasena(req, res, session, usuario);
        } else if ("marcarPrincipal".equals(accion)) {
            handleMarcarPrincipal(req, res, usuario);
        } else if ("agregarDireccion".equals(accion)) {
            handleAgregarDireccion(req, res, usuario);
        } else if ("editarDireccion".equals(accion)) {
            handleEditarDireccion(req, res, usuario);
        } else if ("eliminarDireccion".equals(accion)) {
            handleEliminarDireccion(req, res, usuario);
        } else {
            res.sendRedirect(req.getContextPath() + "/app/cuenta");
        }
    }

    // ── Perfil ────────────────────────────────────────────────
    private void handleActualizarPerfil(HttpServletRequest req, HttpServletResponse res,
            HttpSession session, Usuario usuario)
            throws ServletException, IOException {
        try {
            Usuario actualizado = usuarioBO.actualizarPerfil(
                    usuario.getId(),
                    req.getParameter("nombre"),
                    req.getParameter("telefono"));
            session.setAttribute("clienteLogueado", actualizado);
            session.setAttribute("clienteNombre", actualizado.getNombre());
            req.setAttribute("exito", "Tu información ha sido actualizada correctamente.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        req.setAttribute("seccionActiva", "perfil");
        doGet(req, res);
    }

    private void handleCambiarContrasena(HttpServletRequest req, HttpServletResponse res,
            HttpSession session, Usuario usuario)
            throws ServletException, IOException {
        try {
            usuarioBO.cambiarContrasena(
                    usuario.getId(),
                    req.getParameter("contrasenaActual"),
                    req.getParameter("nuevaContrasena"),
                    req.getParameter("confirmarContrasena"));
            req.setAttribute("exitoContrasena", "Tu contraseña ha sido actualizada correctamente.");
        } catch (Exception e) {
            req.setAttribute("errorContrasena", e.getMessage());
        }
        req.setAttribute("seccionActiva", "perfil");
        doGet(req, res);
    }

    // ── Direcciones ────────────────────────────────────────────
    private void handleAgregarDireccion(HttpServletRequest req, HttpServletResponse res,
            Usuario usuario)
            throws ServletException, IOException {
        try {
            String calle = req.getParameter("calle");
            String ciudad = req.getParameter("ciudad");
            String estado = req.getParameter("estado");
            String cp = req.getParameter("codigoPostal");

            if (calle == null || calle.isBlank()) {
                throw new Exception("La calle es requerida.");
            }
            if (ciudad == null || ciudad.isBlank()) {
                throw new Exception("La ciudad es requerida.");
            }
            if (estado == null || estado.isBlank()) {
                throw new Exception("El estado es requerido.");
            }
            if (cp == null || cp.isBlank()) {
                throw new Exception("El código postal es requerido.");
            }

            // Obtener usuario managed para la FK
            Usuario uManaged = usuarioBO.obtenerPorId(usuario.getId());
            Direccion nueva = new Direccion(calle, ciudad, estado, cp, uManaged);
            direccionDAO.guardar(nueva);
            req.setAttribute("exitoDireccion", "Dirección agregada correctamente.");
        } catch (Exception e) {
            req.setAttribute("errorDireccion", e.getMessage());
        }
        req.setAttribute("seccionActiva", "direcciones");
        doGet(req, res);
    }

    private void handleEditarDireccion(HttpServletRequest req, HttpServletResponse res,
            Usuario usuario)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("direccionId"));
            Direccion dir = direccionDAO.obtenerPorId(id);
            if (dir == null) {
                throw new Exception("Dirección no encontrada.");
            }
            // Verificar que pertenece al usuario
            if (dir.getUsuario() == null || dir.getUsuario().getId() != usuario.getId()) {
                throw new Exception("No tienes permiso para editar esta dirección.");
            }

            dir.setCalle(req.getParameter("calle"));
            dir.setCiudad(req.getParameter("ciudad"));
            dir.setEstado(req.getParameter("estado"));
            dir.setCodigoPostal(req.getParameter("codigoPostal"));
            direccionDAO.actualizar(dir);
            req.setAttribute("exitoDireccion", "Dirección actualizada correctamente.");
        } catch (Exception e) {
            req.setAttribute("errorDireccion", e.getMessage());
        }
        req.setAttribute("seccionActiva", "direcciones");
        doGet(req, res);
    }

    private void handleEliminarDireccion(HttpServletRequest req, HttpServletResponse res,
            Usuario usuario)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("direccionId"));
            Direccion dir = direccionDAO.obtenerPorId(id);
            if (dir == null) {
                throw new Exception("Dirección no encontrada.");
            }
            if (dir.getUsuario() == null || dir.getUsuario().getId() != usuario.getId()) {
                throw new Exception("No tienes permiso para eliminar esta dirección.");
            }
            direccionDAO.eliminar(id);
            req.setAttribute("exitoDireccion", "Dirección eliminada correctamente.");
        } catch (Exception e) {
            req.setAttribute("errorDireccion", e.getMessage());
        }
        req.setAttribute("seccionActiva", "direcciones");
        doGet(req, res);
    }

    private void handleMarcarPrincipal(HttpServletRequest req, HttpServletResponse res,
            Usuario usuario)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("direccionId"));
            Direccion dir = direccionDAO.obtenerPorId(id);
            if (dir == null) throw new Exception("Dirección no encontrada.");
            if (dir.getUsuario() == null || dir.getUsuario().getId() != usuario.getId())
                throw new Exception("No tienes permiso para modificar esta dirección.");
            direccionDAO.marcarPrincipal(id, usuario.getId());
            req.setAttribute("exitoDireccion", "Dirección principal actualizada.");
        } catch (Exception e) {
            req.setAttribute("errorDireccion", e.getMessage());
        }
        req.setAttribute("seccionActiva", "direcciones");
        doGet(req, res);
    }
}
