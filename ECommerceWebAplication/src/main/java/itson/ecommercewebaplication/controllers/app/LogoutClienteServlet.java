package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.models.DetallePedido;
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
@WebServlet(name = "LogoutClienteServlet", urlPatterns = {"/auth/logout"})
public class LogoutClienteServlet extends HttpServlet {

    private CarritoBO carritoBO;

    @Override
    public void init() throws ServletException {
        carritoBO = new CarritoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session != null) {
            // Guardar carrito en BD antes de cerrar sesión
            Usuario usuario = (Usuario) session.getAttribute("clienteLogueado");
            @SuppressWarnings("unchecked")
            List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");

            if (usuario != null) {
                carritoBO.persistirCarrito(usuario, carrito);
            }

            session.invalidate();
        }

        res.sendRedirect(req.getContextPath() + "/auth/login");
    }
}
