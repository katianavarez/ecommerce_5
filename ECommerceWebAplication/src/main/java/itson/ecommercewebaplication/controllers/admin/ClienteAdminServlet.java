package itson.ecommercewebaplication.controllers.admin;

import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.enums.Rol;
import itson.ecommercewebaplication.models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import itson.ecommercewebaplication.util.PaginacionUtil;

/**
 *
 * @author PC
 */
@WebServlet(name = "ClienteAdminServlet", urlPatterns = {"/admin/clientes"})
public class ClienteAdminServlet extends HttpServlet {

    private UsuarioBO usuarioBO;

    @Override
    public void init() throws ServletException {
        usuarioBO = new UsuarioBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String errorParam = req.getParameter("error");
        if (errorParam != null && !errorParam.isBlank()) {
            req.setAttribute("error", errorParam);
        }

        String filtro = req.getParameter("filtro");
        if (filtro == null || filtro.isBlank()) filtro = "activos";
        final String filtroFinal = filtro;

        try {
            List<Usuario> clientes = usuarioBO.obtenerTodos().stream()
                    .filter(u -> u.getRol() == Rol.CLIENTE)
                    .filter(u -> {
                        switch (filtroFinal) {
                            case "todos": return true;
                            case "inactivos": return !u.isActivo();
                            case "activos":
                            default: return u.isActivo();
                        }
                    })
                    .collect(Collectors.toList());
            PaginacionUtil.paginar(req, clientes, "clientes", PaginacionUtil.TAMANO_ADMIN);
            req.setAttribute("filtroActual", filtroFinal);

            // Conteos para los tabs
            List<Usuario> todosClientes = usuarioBO.obtenerTodos().stream()
                    .filter(u -> u.getRol() == Rol.CLIENTE)
                    .collect(Collectors.toList());
            long activos = todosClientes.stream().filter(Usuario::isActivo).count();
            long inactivos = todosClientes.size() - activos;
            req.setAttribute("conteoActivos", activos);
            req.setAttribute("conteoInactivos", inactivos);
            req.setAttribute("conteoTodos", (long) todosClientes.size());
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo cargar la lista de clientes.");
            req.setAttribute("clientes", Collections.emptyList());
        }
        req.getRequestDispatcher("/views/admin/admin-clientes.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        String filtro = req.getParameter("filtro");
        String filtroQs = (filtro != null && !filtro.isBlank()) ? "&filtro=" + filtro : "";
        try {
            if ("eliminar".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                usuarioBO.eliminar(id);
                res.sendRedirect(req.getContextPath() + "/admin/clientes?success=deleted" + filtroQs);
                return;
            }
            if ("reactivar".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                usuarioBO.activar(id);
                res.sendRedirect(req.getContextPath() + "/admin/clientes?success=reactivated" + filtroQs);
                return;
            }
        } catch (Exception e) {
            res.sendRedirect(req.getContextPath()
                + "/admin/clientes?error=" + java.net.URLEncoder.encode(
                    "No se pudo procesar la acción: " + e.getMessage(), "UTF-8"));
        }
    }
}
