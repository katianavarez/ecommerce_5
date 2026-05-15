package itson.ecommercewebaplication.controllers.app;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 *
 * @author PC
 */
@WebServlet(name = "LogoutClienteServlet", urlPatterns = {"/auth/logout"})
public class LogoutClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        res.sendRedirect(req.getContextPath() + "/auth/login");
    }
}
