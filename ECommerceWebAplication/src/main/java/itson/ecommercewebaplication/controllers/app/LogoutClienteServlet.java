package itson.ecommercewebaplication.controllers.app;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Controlador del cierre de sesión del cliente. Igual que el del admin,
 * separa GET y POST como protección anti-CSRF: el GET solo muestra una
 * página intermedia que se autoenvía por POST, y el POST es el que invalida
 * la sesión. Redirige al login con {@code ?logout=true} para que la vista
 * limpie también el token guardado en el localStorage del navegador.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "LogoutClienteServlet", urlPatterns = {"/auth/logout"})
public class LogoutClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Anti-CSRF: un GET (ej. <img src="/auth/logout">) NO debe invalidar sesión.
        // Mostramos una página intermedia con form POST auto-enviado vía JS.
        req.getRequestDispatcher("/views/auth/cliente-logout.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // ?logout=true permite que login.jsp limpie el localStorage del cliente.
        res.sendRedirect(req.getContextPath() + "/auth/login?logout=true");
    }
}
