package itson.ecommercewebaplication.controllers.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Controlador del cierre de sesión del admin. Separa el GET del POST a
 * propósito como medida anti-CSRF: el GET solo muestra una página intermedia
 * con un formulario que se autoenvía por POST, y es el POST el que realmente
 * invalida la sesión. Así un {@code <img src="/admin/logout">} malicioso no
 * puede cerrar la sesión de nadie.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "AdminLogoutServlet", urlPatterns = {"/admin/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Anti-CSRF: un GET (ej. <img src="/admin/logout">) NO debe invalidar sesión.
        // Mostramos una página intermedia con form POST auto-enviado.
        req.getRequestDispatcher("/views/admin/admin-logout.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        res.sendRedirect(req.getContextPath() + "/admin/login");
    }
}
