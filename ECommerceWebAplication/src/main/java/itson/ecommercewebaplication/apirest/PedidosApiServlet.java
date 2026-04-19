package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.enums.FormaPago;
import itson.ecommercewebaplication.models.*;
import itson.ecommercewebaplication.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * 
 * @author PC
 */
@WebServlet(name = "PedidosApiServlet", urlPatterns = {"/api/pedidos", "/api/pedidos/*"})
public class PedidosApiServlet extends HttpServlet {

    private PedidoBO  pedidoBO;
    private UsuarioBO usuarioBO;
    private CarritoBO carritoBO;

    @Override
    public void init() throws ServletException {
        pedidoBO  = new PedidoBO();
        usuarioBO = new UsuarioBO();
        carritoBO = new CarritoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo(); // null | "/{id}" | "/usuario/{id}"

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonUtil.error(res, 400, "Especifica /api/pedidos/{id} o /api/pedidos/usuario/{usuarioId}");
                return;
            }

            String[] parts = pathInfo.split("/"); // ["", "id"] o ["", "usuario", "id"]

            if (parts.length >= 3 && "usuario".equals(parts[1])) {
                // GET /api/pedidos/usuario/{usuarioId}
                int usuarioId = Integer.parseInt(parts[2]);
                List<Pedido> pedidos = pedidoBO.obtenerPorUsuario(usuarioId);
                List<Map<String, Object>> lista = new ArrayList<>();
                for (Pedido p : pedidos) lista.add(toMapSimple(p));
                JsonUtil.ok(res, Map.of("usuarioId", usuarioId, "total", lista.size(), "pedidos", lista));
            } else {
                // GET /api/pedidos/{id}
                int id = Integer.parseInt(parts[1]);
                Pedido pedido = pedidoBO.obtenerPorId(id);
                if (pedido == null) { JsonUtil.error(res, 404, "Pedido no encontrado."); return; }
                JsonUtil.ok(res, toMapDetalle(pedido));
            }
        } catch (NumberFormatException e) {
            JsonUtil.error(res, 400, "ID inválido.");
        } catch (Exception e) {
            JsonUtil.error(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            Map<?, ?> body = JsonUtil.readBody(req, Map.class);
            int usuarioId = ((Number) body.get("usuarioId")).intValue();

            Usuario usuario = usuarioBO.obtenerPorId(usuarioId);
            if (usuario == null) { JsonUtil.error(res, 404, "Usuario no encontrado."); return; }

            // Carrito del usuario
            List<DetallePedido> items = carritoBO.recuperarItemsDesdeDB(usuarioId);
            if (items == null || items.isEmpty()) {
                JsonUtil.error(res, 400, "El carrito está vacío."); return;
            }

            // Dirección de envío
            Direccion dir = new Direccion(
                (String) body.get("calle"),
                (String) body.get("ciudad"),
                (String) body.get("estado"),
                (String) body.get("codigoPostal")
            );

            // Método de pago
            FormaPago formaPago = FormaPago.valueOf(
                ((String) body.get("metodoPago")).toUpperCase());

            Pedido pedido = pedidoBO.crearPedido(usuario, dir, items, formaPago);
            carritoBO.vaciar(usuario);

            res.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.ok(res, toMapDetalle(pedido));

        } catch (IllegalArgumentException e) {
            JsonUtil.error(res, 400, "metodoPago inválido. Usa: TARJETA, EFECTIVO o TRANSFERENCIA.");
        } catch (NullPointerException e) {
            JsonUtil.error(res, 400, "Faltan campos requeridos: usuarioId, calle, ciudad, estado, codigoPostal, metodoPago.");
        } catch (Exception e) {
            JsonUtil.error(res, 500, e.getMessage());
        }
    }

    /** Vista resumida para el historial */
    private Map<String, Object> toMapSimple(Pedido p) {
        return Map.of(
            "id",         p.getId(),
            "numPedido",  p.getNumPedido(),
            "fecha",      p.getFecha().toString(),
            "total",      p.getTotal(),
            "estado",     p.getEstado().name(),
            "numItems",   p.getDetalles() != null ? p.getDetalles().size() : 0
        );
    }

    /** Vista completa para el detalle */
    private Map<String, Object> toMapDetalle(Pedido p) {
        // Items del pedido
        List<Map<String, Object>> detalles = new ArrayList<>();
        if (p.getDetalles() != null) {
            for (DetallePedido d : p.getDetalles()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("productoId",   d.getProducto() != null ? d.getProducto().getId() : null);
                item.put("nombre",       d.getProducto() != null ? d.getProducto().getNombre() : null);
                item.put("imagenURL",    d.getProducto() != null ? d.getProducto().getImagenURL() : null);
                item.put("cantidad",     d.getCantidad());
                item.put("precioUnidad", d.getPrecioUnidad());
                item.put("talla",        d.getTalla());
                item.put("subtotal",     d.getPrecioUnidad() * d.getCantidad());
                detalles.add(item);
            }
        }
        // Dirección
        Map<String, Object> dir = new LinkedHashMap<>();
        if (p.getDireccionEnvio() != null) {
            Direccion d = p.getDireccionEnvio();
            dir.put("calle",        d.getCalle());
            dir.put("ciudad",       d.getCiudad());
            dir.put("estado",       d.getEstado());
            dir.put("codigoPostal", d.getCodigoPostal());
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",             p.getId());
        m.put("numPedido",      p.getNumPedido());
        m.put("fecha",          p.getFecha().toString());
        m.put("total",          p.getTotal());
        m.put("estado",         p.getEstado().name());
        m.put("metodoPago",     p.getPago() != null ? p.getPago().getMetodo().name() : null);
        m.put("direccionEnvio", dir);
        m.put("detalles",       detalles);
        m.put("usuarioId",      p.getUsuario() != null ? p.getUsuario().getId() : null);
        return m;
    }
}
