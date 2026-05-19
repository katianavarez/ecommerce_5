package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.enums.EstadoPedido;
import itson.ecommercewebaplication.enums.FormaPago;
import itson.ecommercewebaplication.models.*;
import itson.ecommercewebaplication.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * Endpoint REST de pedidos del cliente autenticado. Expone POST /api/pedidos
 * (checkout: crea el pedido a partir del carrito y los datos de envío/pago),
 * GET /api/pedidos/{id} (detalle), GET /api/pedidos/usuario/{id} (historial)
 * y DELETE /api/pedidos/{id} (cancelar). Comprueba en cada consulta que el
 * pedido o el historial pertenezcan al usuario del token (responde 403 si
 * no), y en el checkout valida server-side los datos de tarjeta cuando la
 * forma de pago es TARJETA, para que un POST directo no salte la validación
 * del formulario.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "PedidosApiServlet", urlPatterns = {"/api/pedidos", "/api/pedidos/*"})
public class PedidosApiServlet extends HttpServlet {

    private PedidoBO  pedidoBO;
    private CarritoBO carritoBO;

    @Override
    public void init() throws ServletException {
        pedidoBO  = new PedidoBO();
        carritoBO = new CarritoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Usuario usuario = (Usuario) req.getAttribute("usuarioAuth");
        if (usuario == null) { JsonUtil.error(res, 401, "No autenticado."); return; }

        String pathInfo = req.getPathInfo(); // null | "/{id}" | "/usuario/{id}"

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonUtil.error(res, 400, "Especifica /api/pedidos/{id} o /api/pedidos/usuario/{usuarioId}");
                return;
            }

            String[] parts = pathInfo.split("/"); // ["", "id"] o ["", "usuario", "id"]

            if (parts.length >= 3 && "usuario".equals(parts[1])) {
                int usuarioId = Integer.parseInt(parts[2]);
                if (usuarioId != usuario.getId()) {
                    JsonUtil.error(res, 403, "Solo puedes consultar tu propio historial de pedidos.");
                    return;
                }
                List<Pedido> pedidos = pedidoBO.obtenerPorUsuario(usuarioId);
                List<Map<String, Object>> lista = new ArrayList<>();
                for (Pedido p : pedidos) lista.add(toMapSimple(p));
                JsonUtil.ok(res, Map.of("usuarioId", usuarioId, "total", lista.size(), "pedidos", lista));
            } else {
                int id = Integer.parseInt(parts[1]);
                Pedido pedido = pedidoBO.obtenerPorId(id);
                if (pedido == null) { JsonUtil.error(res, 404, "Pedido no encontrado."); return; }
                if (pedido.getUsuario() == null || pedido.getUsuario().getId() != usuario.getId()) {
                    JsonUtil.error(res, 403, "No puedes ver este pedido.");
                    return;
                }
                JsonUtil.ok(res, toMapDetalle(pedido));
            }
        } catch (NumberFormatException e) {
            JsonUtil.error(res, 400, "ID inválido.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.error(res, 500, "Error interno del servidor.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Usuario usuario = (Usuario) req.getAttribute("usuarioAuth");
        if (usuario == null) { JsonUtil.error(res, 401, "No autenticado."); return; }

        try {
            Map<?, ?> body = JsonUtil.readBody(req, Map.class);

            // Carrito del usuario (siempre tomado de BD para el usuario autenticado)
            List<DetallePedido> items = carritoBO.recuperarItemsDesdeDB(usuario.getId());
            if (items == null || items.isEmpty()) {
                JsonUtil.error(res, 400, "El carrito está vacío."); return;
            }

            Direccion dir = new Direccion(
                (String) body.get("calle"),
                (String) body.get("ciudad"),
                (String) body.get("estado"),
                (String) body.get("codigoPostal")
            );

            Object metodoRaw = body.get("metodoPago");
            if (!(metodoRaw instanceof String) || ((String) metodoRaw).isBlank()) {
                JsonUtil.error(res, 400, "metodoPago es requerido.");
                return;
            }
            FormaPago formaPago;
            try {
                formaPago = FormaPago.valueOf(((String) metodoRaw).toUpperCase());
            } catch (IllegalArgumentException ex) {
                JsonUtil.error(res, 400, "metodoPago inválido. Usa: TARJETA, TRANSFERENCIA o CONTRA_ENTREGA.");
                return;
            }

            // Pasarela simulada: si paga con tarjeta validamos formato server-side
            // para que un POST directo no pueda saltarse la validación del checkout.jsp.
            if (formaPago == FormaPago.TARJETA) {
                String numTarjeta = asStrSafe(body.get("numTarjeta"));
                String vencimiento = asStrSafe(body.get("vencimiento"));
                String cvv = asStrSafe(body.get("cvv"));
                if (numTarjeta == null || !numTarjeta.replaceAll("\\s", "").matches("\\d{16}")) {
                    JsonUtil.error(res, 400, "Número de tarjeta inválido. Debe tener 16 dígitos.");
                    return;
                }
                if (vencimiento == null || !vencimiento.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                    JsonUtil.error(res, 400, "Vencimiento inválido. Formato MM/AA.");
                    return;
                }
                if (cvv == null || !cvv.matches("\\d{3,4}")) {
                    JsonUtil.error(res, 400, "CVV inválido. Debe tener 3 o 4 dígitos.");
                    return;
                }
            }

            Pedido pedido = pedidoBO.crearPedido(usuario, dir, items, formaPago);
            carritoBO.vaciar(usuario);

            // Sincronizar session.carrito para que el badge del header y las
            // siguientes JSPs (confirmacion, productos...) reflejen el carrito vacío.
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.setAttribute("carrito", new java.util.ArrayList<>());
            }

            JsonUtil.created(res, toMapDetalle(pedido));

        } catch (NullPointerException | ClassCastException e) {
            JsonUtil.error(res, 400, "Faltan o tipo inválido en campos requeridos: calle, ciudad, estado, codigoPostal, metodoPago.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.error(res, 500, "Error interno del servidor.");
        }
    }

    private static String asStrSafe(Object v) {
        return v == null ? null : v.toString();
    }

    /**
     * DELETE /api/pedidos/{id} — cancela el pedido del usuario autenticado.
     * Solo permitido si el pedido pertenece al usuario y no está ENTREGADO.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Usuario usuario = (Usuario) req.getAttribute("usuarioAuth");
        if (usuario == null) { JsonUtil.error(res, 401, "No autenticado."); return; }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.error(res, 400, "Falta el id del pedido en la ruta: DELETE /api/pedidos/{id}");
            return;
        }
        try {
            int id = Integer.parseInt(pathInfo.substring(1).split("/")[0]);
            Pedido pedido = pedidoBO.obtenerPorId(id);
            if (pedido == null) { JsonUtil.error(res, 404, "Pedido no encontrado."); return; }
            if (pedido.getUsuario() == null || pedido.getUsuario().getId() != usuario.getId()) {
                JsonUtil.error(res, 403, "No puedes cancelar este pedido.");
                return;
            }
            if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
                JsonUtil.error(res, 409, "No se puede cancelar un pedido ya entregado.");
                return;
            }
            if (pedido.getEstado() == EstadoPedido.CANCELADO) {
                JsonUtil.noContent(res);
                return;
            }
            pedidoBO.cancelarPedido(id);
            JsonUtil.noContent(res);
        } catch (NumberFormatException e) {
            JsonUtil.error(res, 400, "ID inválido.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.error(res, 500, "Error interno del servidor.");
        }
    }

    /** Vista resumida para el historial. Incluye los primeros 3 productos
     *  (imagen + nombre) para mostrar las thumbnails en la card. */
    private Map<String, Object> toMapSimple(Pedido p) {
        List<Map<String, Object>> thumbs = new ArrayList<>();
        if (p.getDetalles() != null) {
            int n = Math.min(3, p.getDetalles().size());
            for (int i = 0; i < n; i++) {
                DetallePedido d = p.getDetalles().get(i);
                if (d.getProducto() != null) {
                    Map<String, Object> t = new LinkedHashMap<>();
                    t.put("imagenURL", d.getProducto().getImagenURL());
                    t.put("nombre",    d.getProducto().getNombre());
                    thumbs.add(t);
                }
            }
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",         p.getId());
        m.put("numPedido",  p.getNumPedido());
        m.put("fecha",      p.getFecha() != null ? p.getFecha().toString() : null);
        m.put("total",      p.getTotal());
        m.put("estado",     p.getEstado() != null ? p.getEstado().name() : null);
        m.put("numItems",   p.getDetalles() != null ? p.getDetalles().size() : 0);
        m.put("thumbnails", thumbs);
        return m;
    }

    /** Vista completa para el detalle */
    private Map<String, Object> toMapDetalle(Pedido p) {
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
