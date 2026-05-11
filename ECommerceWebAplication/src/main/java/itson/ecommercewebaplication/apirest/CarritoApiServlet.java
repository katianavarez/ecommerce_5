package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.CarritoBO;
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

    @Override
    public void init() throws ServletException {
        carritoBO = new CarritoBO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Usuario usuario = (Usuario) req.getAttribute("usuarioAuth");
        if (usuario == null) {
            JsonUtil.error(res, 401, "No autenticado.");
            return;
        }

        try {
            Map<?, ?> body = JsonUtil.readBody(req, Map.class);
            int productoId = ((Number) body.get("productoId")).intValue();
            int cantidad = ((Number) body.get("cantidad")).intValue();
            String talla = body.get("talla") != null ? body.get("talla").toString() : null;
            if (talla != null && talla.isBlank()) {
                talla = null;
            }

            Carrito carrito = carritoBO.agregarItem(usuario, productoId, cantidad, talla);
            JsonUtil.ok(res, toMap(carrito));
        } catch (IllegalArgumentException | NullPointerException e) {
            JsonUtil.error(res, 400, "Body inválido. Campos requeridos: productoId, cantidad. Opcional: talla.");
        } catch (Exception e) {
            JsonUtil.error(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Usuario usuario = (Usuario) req.getAttribute("usuarioAuth");
        if (usuario == null) {
            JsonUtil.error(res, 401, "No autenticado.");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                int pathId = Integer.parseInt(pathInfo.substring(1).split("/")[0]);
                if (pathId != usuario.getId()) {
                    JsonUtil.error(res, 403, "No puedes consultar el carrito de otro usuario.");
                    return;
                }
            } catch (NumberFormatException e) {
                JsonUtil.error(res, 400, "usuarioId inválido.");
                return;
            }
        }

        try {
            Carrito carrito = carritoBO.obtenerPorUsuario(usuario.getId());
            if (carrito == null) {
                JsonUtil.ok(res, Map.of("usuarioId", usuario.getId(), "items", List.of(), "total", 0.0));
                return;
            }
            JsonUtil.ok(res, toMap(carrito));
        } catch (Exception e) {
            JsonUtil.error(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Usuario usuario = (Usuario) req.getAttribute("usuarioAuth");
        if (usuario == null) {
            JsonUtil.error(res, 401, "No autenticado.");
            return;
        }

        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                carritoBO.vaciar(usuario);
                JsonUtil.ok(res, Map.of("success", true, "message", "Carrito vaciado."));
                return;
            }
            String[] parts = pathInfo.split("/");
            if (parts.length < 3 || !"producto".equals(parts[1])) {
                JsonUtil.error(res, 400, "Ruta no reconocida.");
                return;
            }
            int productoId = Integer.parseInt(parts[2]);
            String talla = null;
            if (parts.length >= 5 && "talla".equals(parts[3])) {
                talla = parts[4];
                if (talla.isBlank()) {
                    talla = null;
                }
            }
            carritoBO.eliminarItem(usuario, productoId, talla);
            JsonUtil.ok(res, Map.of("success", true, "message", "Item eliminado del carrito."));
        } catch (NumberFormatException e) {
            JsonUtil.error(res, 400, "productoId inválido.");
        } catch (Exception e) {
            JsonUtil.error(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Usuario usuario = (Usuario) req.getAttribute("usuarioAuth");
        if (usuario == null) {
            JsonUtil.error(res, 401, "No autenticado.");
            return;
        }

        try {
            Map<?, ?> body = JsonUtil.readBody(req, Map.class);
            int productoId = ((Number) body.get("productoId")).intValue();
            int cantidad = ((Number) body.get("cantidad")).intValue();
            String talla = body.get("talla") != null ? body.get("talla").toString() : null;
            if (talla != null && talla.isBlank()) {
                talla = null;
            }

            if (cantidad <= 0) {
                carritoBO.eliminarItem(usuario, productoId, talla);
                Carrito carrito = carritoBO.obtenerPorUsuario(usuario.getId());
                JsonUtil.ok(res, carrito != null ? toMap(carrito)
                        : Map.of("usuarioId", usuario.getId(), "items", List.of(), "total", 0.0));
                return;
            }

            carritoBO.eliminarItem(usuario, productoId, talla);
            Carrito carrito = carritoBO.agregarItem(usuario, productoId, cantidad, talla);
            JsonUtil.ok(res, toMap(carrito));
        } catch (IllegalArgumentException | NullPointerException e) {
            JsonUtil.error(res, 400, "Body inválido. Campos requeridos: productoId, cantidad. Opcional: talla.");
        } catch (Exception e) {
            JsonUtil.error(res, 500, e.getMessage());
        }
    }

    private Map<String, Object> toMap(Carrito c) {
        List<Map<String, Object>> items = new ArrayList<>();
        if (c.getDetalles() != null) {
            for (DetallePedido d : c.getDetalles()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("productoId", d.getProducto() != null ? d.getProducto().getId() : null);
                item.put("nombre", d.getProducto() != null ? d.getProducto().getNombre() : null);
                item.put("imagenURL", d.getProducto() != null ? d.getProducto().getImagenURL() : null);
                item.put("precioUnidad", d.getPrecioUnidad());
                item.put("cantidad", d.getCantidad());
                item.put("talla", d.getTalla());
                item.put("subtotal", d.getPrecioUnidad() * d.getCantidad());
                items.add(item);
            }
        }
        return Map.of(
                "carritoId", c.getId(),
                "usuarioId", c.getUsuario() != null ? c.getUsuario().getId() : null,
                "items", items,
                "total", c.getTotal()
        );
    }
}
