package itson.ecommercewebaplication.controllers.admin;

import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 *
 * @author PC
 */
@WebServlet(name = "AdminLoginServlet", urlPatterns = {"/admin/login"})
public class LoginServlet extends HttpServlet {

    private UsuarioBO usuarioBO;

    @Override
    public void init() throws ServletException {
        usuarioBO = new UsuarioBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            res.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }
        req.getRequestDispatcher("/views/admin/admin-login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String correo = req.getParameter("correo");
        String contrasena = req.getParameter("contrasena");
        try {
            Usuario usuario = usuarioBO.login(correo, contrasena);
            if (!usuarioBO.esAdministrador(usuario)) {
                req.setAttribute("error", "Acceso denegado. Solo administradores.");
                req.getRequestDispatcher("/views/admin/admin-login.jsp").forward(req, res);
                return;
            }
            HttpSession session = req.getSession();
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("esAdmin", true);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setMaxInactiveInterval(30 * 60);
            res.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("correo", correo);
            req.getRequestDispatcher("/views/admin/admin-login.jsp").forward(req, res);
        }
    }
}
