package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.dto.CarritoItemRequestDTO;
import itson.ecommercewebaplication.models.Carrito;
import itson.ecommercewebaplication.models.DetallePedido;
import itson.ecommercewebaplication.models.Usuario;
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
@WebServlet(name = "CarritoApiServlet", urlPatterns = {"/api/carrito", "/api/carrito/*"})
public class CarritoApiServlet extends HttpServlet {

    private CarritoBO carritoBO;
    private UsuarioBO usuarioBO;

    @Override
    public void init() throws ServletException {
        carritoBO = new CarritoBO();
        usuarioBO = new UsuarioBO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            Map<?, ?> body = JsonUtil.readBody(req, Map.class);
            int usuarioId  = ((Number) body.get("usuarioId")).intValue();
            int productoId = ((Number) body.get("productoId")).intValue();
            int cantidad   = ((Number) body.get("cantidad")).intValue();

            Usuario usuario = usuarioBO.obtenerPorId(usuarioId);
            if (usuario == null) { JsonUtil.error(res, 404, "Usuario no encontrado."); return; }

            Carrito carrito = carritoBO.agregarItem(usuario, productoId, cantidad);
            JsonUtil.ok(res, toMap(carrito));
        } catch (IllegalArgumentException | NullPointerException e) {
            JsonUtil.error(res, 400, "Body inválido. Campos requeridos: usuarioId, productoId, cantidad.");
        } catch (Exception e) {
            JsonUtil.error(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.error(res, 400, "Falta el usuarioId. Usa GET /api/carrito/{usuarioId}");
            return;
        }
        try {
            String[] parts = pathInfo.split("/");
            int usuarioId = Integer.parseInt(parts[1]);
            Carrito carrito = carritoBO.obtenerPorUsuario(usuarioId);
            if (carrito == null) {
                JsonUtil.ok(res, Map.of("usuarioId", usuarioId, "items", List.of(), "total", 0.0));
                return;
            }
            JsonUtil.ok(res, toMap(carrito));
        } catch (NumberFormatException e) {
            JsonUtil.error(res, 400, "usuarioId inválido.");
        } catch (Exception e) {
            JsonUtil.error(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo(); 
        try {
            String[] parts = pathInfo.split("/");
            int usuarioId  = Integer.parseInt(parts[1]);
            int productoId = Integer.parseInt(parts[3]);
            Usuario usuario = usuarioBO.obtenerPorId(usuarioId);
            if (usuario == null) { JsonUtil.error(res, 404, "Usuario no encontrado."); return; }
            carritoBO.eliminarItem(usuario, productoId);
            JsonUtil.ok(res, Map.of("success", true, "message", "Item eliminado del carrito."));
        } catch (Exception e) {
            JsonUtil.error(res, 500, e.getMessage());
        }
    }

    private Map<String, Object> toMap(Carrito c) {
        List<Map<String, Object>> items = new ArrayList<>();
        if (c.getDetalles() != null) {
            for (DetallePedido d : c.getDetalles()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("productoId",   d.getProducto() != null ? d.getProducto().getId() : null);
                item.put("nombre",       d.getProducto() != null ? d.getProducto().getNombre() : null);
                item.put("imagenURL",    d.getProducto() != null ? d.getProducto().getImagenURL() : null);
                item.put("precioUnidad", d.getPrecioUnidad());
                item.put("cantidad",     d.getCantidad());
                item.put("talla",        d.getTalla());
                item.put("subtotal",     d.getPrecioUnidad() * d.getCantidad());
                items.add(item);
            }
        }
        return Map.of(
            "carritoId", c.getId(),
            "usuarioId", c.getUsuario() != null ? c.getUsuario().getId() : null,
            "items",     items,
            "total",     c.getTotal()
        );
    }
}
