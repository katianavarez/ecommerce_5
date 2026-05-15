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
 *
 * @author PC
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/app/checkout"})
public class CheckoutServlet extends HttpServlet {

    private PedidoBO pedidoBO;
    private CarritoBO carritoBO;
    private DireccionDAO direccionDAO;

    @Override
    public void init() throws ServletException {
        pedidoBO = new PedidoBO();
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        try {
            Usuario usuario = (Usuario) session.getAttribute("clienteLogueado");
            if (usuario == null) {
                throw new Exception("Debe iniciar sesión para continuar.");
            }

            List<DetallePedido> carrito = carritoBO.recuperarItemsDesdeDB(usuario.getId());
            if (carrito == null || carrito.isEmpty()) {
                throw new Exception("El carrito está vacío.");
            }

            Direccion direccion = new Direccion(
                    req.getParameter("calle"),
                    req.getParameter("ciudad"),
                    req.getParameter("estado"),
                    req.getParameter("codigoPostal")
            );
            FormaPago metodoPago = FormaPago.valueOf(req.getParameter("metodoPago"));
            Pedido pedido = pedidoBO.crearPedido(usuario, direccion, carrito, metodoPago);

            try {
                carritoBO.vaciar(usuario);
            } catch (Exception ignored) {
            }
            session.removeAttribute("carrito");

            res.sendRedirect(req.getContextPath() + "/app/confirmacion?pedidoId=" + pedido.getId());
        } catch (Exception e) {
            req.setAttribute("error", "Error al procesar el pedido: " + e.getMessage());
            doGet(req, res);
        }
    }
}
