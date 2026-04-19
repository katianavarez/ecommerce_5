package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.models.Pedido;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 *
 * @author PC
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
            req.setAttribute("pedido", pedido);
            req.getRequestDispatcher("/views/aplication/confirmacion.jsp").forward(req, res);
        } catch (Exception e) {
            res.sendRedirect(req.getContextPath() + "/app/productos");
        }
    }
}
