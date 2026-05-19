package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.dao.DireccionDAO;
import itson.ecommercewebaplication.enums.FormaPago;
import itson.ecommercewebaplication.models.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Controlador de la pantalla de checkout. Solo atiende el GET: arma el
 * resumen de la compra (items, subtotal, costo de envío y total) y precarga
 * la dirección principal del cliente. La confirmación del pedido NO se hace
 * aquí, sino por POST a /api/pedidos desde checkout.js; se quitó el doPost a
 * propósito para que no exista el riesgo de crear el pedido dos veces (una
 * por el servlet y otra por el fetch).
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/app/checkout"})
public class CheckoutServlet extends HttpServlet {

    private CarritoBO carritoBO;
    private DireccionDAO direccionDAO;

    @Override
    public void init() throws ServletException {
        carritoBO = new CarritoBO();
        direccionDAO = new DireccionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        Usuario usuario = (Usuario) session.getAttribute("clienteLogueado");
        if (usuario == null) {
            res.sendRedirect(req.getContextPath() + "/auth/login?redirect=/app/checkout");
            return;
        }

        List<DetallePedido> carrito = carritoBO.recuperarItemsDesdeDB(usuario.getId());
        session.setAttribute("carrito", carrito);
        if (carrito == null || carrito.isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/app/carrito");
            return;
        }

        double subtotal = carrito.stream().mapToDouble(d -> d.getPrecioUnidad() * d.getCantidad()).sum();
        double costoEnvio = PedidoBO.calcularEnvio(subtotal);

        List<Direccion> direcciones = direccionDAO.obtenerPorUsuario(usuario.getId());
        Direccion direccionPrincipal = direcciones.isEmpty() ? null : direcciones.get(0);

        req.setAttribute("usuario", usuario);
        req.setAttribute("carrito", carrito);
        req.setAttribute("subtotal", subtotal);
        req.setAttribute("costoEnvio", costoEnvio);
        req.setAttribute("total", subtotal + costoEnvio);
        req.setAttribute("metodosPago", FormaPago.values());
        req.setAttribute("direccionPrincipal", direccionPrincipal);
        req.getRequestDispatcher("/views/aplication/checkout.jsp").forward(req, res);
    }

    // doPost retirado: la creación del pedido se hace 100% por POST /api/pedidos
    // (PedidosApiServlet) consumido por checkout.js vía Fetch. Esto elimina el
    // riesgo de pedido duplicado por doble envío (servlet + fetch).
}
