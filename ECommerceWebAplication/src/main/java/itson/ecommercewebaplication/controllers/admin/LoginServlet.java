package itson.ecommercewebaplication.controllers.admin;

import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Controlador del login del panel de administración. El GET muestra el
 * formulario (o redirige al dashboard si ya hay sesión); el POST valida las
 * credenciales y, además, comprueba que el usuario tenga rol de administrador
 * antes de dejarlo entrar. Rota el JSESSIONID al autenticar para mitigar
 * session fixation y usa un mensaje de error uniforme para no revelar si un
 * correo existe o no.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
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
                // Mensaje uniforme: no revelamos que el correo existe pero no es admin.
                req.setAttribute("error", "Credenciales inválidas.");
                req.setAttribute("correo", correo);
                req.getRequestDispatcher("/views/admin/admin-login.jsp").forward(req, res);
                return;
            }
            // Rotar el JSESSIONID para evitar session fixation antes de poblar la sesión.
            HttpSession session = req.getSession(true);
            req.changeSessionId();
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("esAdmin", true);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setMaxInactiveInterval(30 * 60);
            res.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } catch (Exception e) {
            // Mensaje genérico para evitar enumeración de cuentas en el panel admin.
            req.setAttribute("error", "Credenciales inválidas.");
            req.setAttribute("correo", correo);
            req.getRequestDispatcher("/views/admin/admin-login.jsp").forward(req, res);
        }
    }
}
