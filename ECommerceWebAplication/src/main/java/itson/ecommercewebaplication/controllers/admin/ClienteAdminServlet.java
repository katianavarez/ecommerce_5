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
        try {
            List<Usuario> clientes = usuarioBO.obtenerTodos().stream()
                    .filter(u -> u.getRol() == Rol.CLIENTE)
                    .collect(Collectors.toList());
            PaginacionUtil.paginar(req, clientes, "clientes", PaginacionUtil.TAMANO_ADMIN);
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
        try {
            if ("eliminar".equals(req.getParameter("accion"))) {
                usuarioBO.eliminar(Integer.parseInt(req.getParameter("id")));
                res.sendRedirect(req.getContextPath() + "/admin/clientes?success=deleted");
            }
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo eliminar el cliente. Intenta nuevamente.");
            doGet(req, res);
        }
    }
}
