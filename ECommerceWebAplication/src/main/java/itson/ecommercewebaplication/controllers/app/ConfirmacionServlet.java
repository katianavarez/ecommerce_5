package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.models.Pedido;
import itson.ecommercewebaplication.models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Controlador de la pantalla de confirmación de pedido. Muestra el detalle de
 * un pedido recién hecho (o consultado desde el historial) incluyendo su
 * número único. Antes de mostrarlo verifica que el pedido pertenezca al
 * cliente en sesión y, si no, responde 403; así nadie puede ver el pedido de
 * otro cambiando el {@code pedidoId} de la URL.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "ConfirmacionServlet", urlPatterns = {"/app/confirmacion"})
public class ConfirmacionServlet extends HttpServlet {

    private PedidoBO pedidoBO;

    @Override
    public void init() throws ServletException {
        pedidoBO = new PedidoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            int pedidoId = Integer.parseInt(req.getParameter("pedidoId"));
            Pedido pedido = pedidoBO.obtenerPorId(pedidoId);
            if (pedido == null) {
                res.sendRedirect(req.getContextPath() + "/app/productos");
                return;
            }
            HttpSession session = req.getSession(false);
            Usuario usuario = session != null
                    ? (Usuario) session.getAttribute("clienteLogueado") : null;
            if (usuario == null || pedido.getUsuario() == null
                    || pedido.getUsuario().getId() != usuario.getId()) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            req.setAttribute("pedido", pedido);
            req.getRequestDispatcher("/views/aplication/confirmacion.jsp").forward(req, res);
        } catch (Exception e) {
            res.sendRedirect(req.getContextPath() + "/app/productos");
        }
    }
}
