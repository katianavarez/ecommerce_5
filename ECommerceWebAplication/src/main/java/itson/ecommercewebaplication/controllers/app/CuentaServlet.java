package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.dao.DireccionDAO;
import itson.ecommercewebaplication.models.Direccion;
import itson.ecommercewebaplication.models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author PC
 */
@WebServlet(name = "CuentaServlet", urlPatterns = {"/app/cuenta"})
public class CuentaServlet extends HttpServlet {

    private PedidoBO pedidoBO;
    private UsuarioBO usuarioBO;
    private DireccionDAO direccionDAO;

    @Override
    public void init() throws ServletException {
        pedidoBO = new PedidoBO();
        usuarioBO = new UsuarioBO();
        direccionDAO = new DireccionDAO();
    }

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

        List<itson.ecommercewebaplication.models.Pedido> todosPedidos
                = pedidoBO.obtenerPorUsuario(usuario.getId());
        int TAMANO_PEDIDOS = 5;
        int totalPedidos = todosPedidos.size();
        int totalPagPed = (totalPedidos == 0) ? 1
                : (int) Math.ceil((double) totalPedidos / TAMANO_PEDIDOS);
        String pPedStr = req.getParameter("paginaPedidos");
        int paginaPedidos = 1;
        if (pPedStr != null && !pPedStr.isBlank()) {
            try {
                paginaPedidos = Integer.parseInt(pPedStr);
            } catch (NumberFormatException ignored) {
            }
        }
        if (paginaPedidos < 1) {
            paginaPedidos = 1;
        }
        if (paginaPedidos > totalPagPed) {
            paginaPedidos = totalPagPed;
        }
        int desde = (paginaPedidos - 1) * TAMANO_PEDIDOS;
        int hasta = Math.min(desde + TAMANO_PEDIDOS, totalPedidos);

        req.setAttribute("pedidos", todosPedidos.subList(desde, hasta));
        req.setAttribute("totalPedidos", totalPedidos);
        req.setAttribute("totalPagPed", totalPagPed);
        req.setAttribute("paginaPedidos", paginaPedidos);

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
