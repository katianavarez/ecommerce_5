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
 * Endpoint REST del carrito del cliente autenticado. Cubre POST (agregar),
 * GET (ver carrito), PUT (cambiar cantidad) y DELETE (quitar un item o vaciar
 * todo). Todas las operaciones requieren JWT y trabajan siempre sobre el
 * carrito del usuario que viene en el token; al consultar por id ajeno
 * responde 403 para evitar que alguien vea el carrito de otro. Tras cada
 * cambio sincroniza el atributo de sesión que alimenta el contador del
 * header en las páginas renderizadas por el servidor.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
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
            int productoId = asInt(body.get("productoId"));
            int cantidad = asInt(body.get("cantidad"));
            String talla = body.get("talla") != null ? body.get("talla").toString() : null;
            if (talla != null && talla.isBlank()) {
                talla = null;
            }

            Carrito carrito = carritoBO.agregarItem(usuario, productoId, cantidad, talla);
            actualizarSessionCarrito(req, carrito);
            JsonUtil.created(res, toMap(carrito));
        } catch (IllegalArgumentException | NullPointerException | ClassCastException e) {
            JsonUtil.error(res, 400, "Body inválido. Campos requeridos: productoId (int), cantidad (int). Opcional: talla.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.error(res, 500, "Error interno del servidor.");
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
            e.printStackTrace();
            JsonUtil.error(res, 500, "Error interno del servidor.");
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
                actualizarSessionCarrito(req, null);
                JsonUtil.noContent(res);
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
            // Rehidratar session.carrito releyendo el carrito actualizado de BD.
            Carrito tras = carritoBO.obtenerPorUsuario(usuario.getId());
            actualizarSessionCarrito(req, tras);
            JsonUtil.noContent(res);
        } catch (NumberFormatException e) {
            JsonUtil.error(res, 400, "productoId inválido.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.error(res, 500, "Error interno del servidor.");
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
            int productoId = asInt(body.get("productoId"));
            int cantidad = asInt(body.get("cantidad"));
            String talla = body.get("talla") != null ? body.get("talla").toString() : null;
            if (talla != null && talla.isBlank()) {
                talla = null;
            }

            int cantidadNorm = Math.max(0, cantidad);
            Carrito carrito = carritoBO.actualizarCantidad(usuario, productoId, cantidadNorm, talla);
            actualizarSessionCarrito(req, carrito);
            JsonUtil.ok(res, carrito != null ? toMap(carrito)
                    : Map.of("usuarioId", usuario.getId(), "items", List.of(), "total", 0.0));
        } catch (IllegalArgumentException | NullPointerException | ClassCastException e) {
            JsonUtil.error(res, 400, "Body inválido. Campos requeridos: productoId (int), cantidad (int). Opcional: talla.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.error(res, 500, "Error interno del servidor.");
        }
    }

    /**
     * Mantiene sincronizado el atributo {@code carrito} de la HttpSession con la BD
     * después de cada mutación, para que el badge del header en las JSPs autenticadas
     * refleje el estado real sin esperar a la próxima visita a /app/carrito.
     */
    private void actualizarSessionCarrito(HttpServletRequest req, Carrito carrito) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        List<DetallePedido> items = (carrito != null && carrito.getDetalles() != null)
                ? carrito.getDetalles()
                : new ArrayList<>();
        session.setAttribute("carrito", items);
    }

    /** Acepta Number o String numérico. Lanza IllegalArgumentException si llega null o no es numérico. */
    private static int asInt(Object v) {
        if (v == null) throw new IllegalArgumentException("valor requerido");
        if (v instanceof Number) return ((Number) v).intValue();
        return Integer.parseInt(v.toString().trim());
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
