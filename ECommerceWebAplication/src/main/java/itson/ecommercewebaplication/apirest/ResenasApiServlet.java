package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.bo.ProductoBO;
import itson.ecommercewebaplication.bo.ResenaBO;
import itson.ecommercewebaplication.models.Producto;
import itson.ecommercewebaplication.models.Resenia;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 *
 * @author PC
 */
@WebServlet(name = "ResenasApiServlet", urlPatterns = {"/api/resenas", "/api/resenas/*"})
public class ResenasApiServlet extends HttpServlet {

    private ResenaBO  resenaBO;
    private ProductoBO productoBO;
    private PedidoBO  pedidoBO;

    @Override
    public void init() throws ServletException {
        resenaBO   = new ResenaBO();
        productoBO = new ProductoBO();
        pedidoBO   = new PedidoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonUtil.error(res, 400, "Usa GET /api/resenas/producto/{productoId}");
                return;
            }
            String[] parts = pathInfo.split("/");
            if (parts.length >= 3 && "producto".equals(parts[1])) {
                int productoId = Integer.parseInt(parts[2]);
                List<Resenia> resenias = resenaBO.obtenerRecientesPorProducto(productoId, 0, 50);
                double promedio = resenaBO.calcularPromedio(productoId);
                long total      = resenaBO.contarPorProducto(productoId);

                List<Map<String, Object>> lista = new ArrayList<>();
                for (Resenia r : resenias) lista.add(toMap(r));

                JsonUtil.ok(res, Map.of(
                    "productoId", productoId,
                    "promedio",   Math.round(promedio * 10.0) / 10.0,
                    "total",      total,
                    "resenas",    lista
                ));
            } else {
                JsonUtil.error(res, 400, "Ruta no reconocida. Usa /api/resenas/producto/{id}");
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
        Usuario usuario = (Usuario) req.getAttribute("usuarioAuth");
        if (usuario == null) { JsonUtil.error(res, 401, "No autenticado."); return; }

        try {
            Map<?, ?> body       = JsonUtil.readBody(req, Map.class);
            int productoId       = ((Number) body.get("productoId")).intValue();
            int calificacion     = ((Number) body.get("calificacion")).intValue();
            String comentario    = (String) body.get("comentario");

            if (calificacion < 1 || calificacion > 5) {
                JsonUtil.error(res, 400, "calificacion debe estar entre 1 y 5."); return;
            }

            Producto producto = productoBO.obtenerPorId(productoId);
            if (producto == null) { JsonUtil.error(res, 404, "Producto no encontrado."); return; }

            int usuarioId = usuario.getId();
            if (!pedidoBO.usuarioComproProducto(usuarioId, productoId)) {
                JsonUtil.error(res, 403, "Solo puedes reseñar productos que hayas comprado."); return;
            }

            long compras  = pedidoBO.contarComprasDeProducto(usuarioId, productoId);
            long dadas    = resenaBO.contarResenasPorUsuarioYProducto(usuarioId, productoId);
            if (dadas >= compras) {
                JsonUtil.error(res, 409, "Ya has dejado el máximo de reseñas permitidas para este producto."); return;
            }

            Resenia nueva = new Resenia(calificacion, comentario, LocalDate.now(), producto, usuario);
            Resenia creada = resenaBO.crear(nueva);

            res.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.ok(res, toMap(creada));

        } catch (NullPointerException e) {
            JsonUtil.error(res, 400, "Faltan campos: productoId, calificacion.");
        } catch (Exception e) {
            JsonUtil.error(res, 500, e.getMessage());
        }
    }

    private Map<String, Object> toMap(Resenia r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",           r.getId());
        m.put("calificacion", r.getCalificacion());
        m.put("comentario",   r.getComentario());
        m.put("fecha",        r.getFecha() != null ? r.getFecha().toString() : null);
        m.put("usuarioId",    r.getUsuario()  != null ? r.getUsuario().getId()   : null);
        m.put("usuarioNombre",r.getUsuario()  != null ? r.getUsuario().getNombre(): null);
        m.put("productoId",   r.getProducto() != null ? r.getProducto().getId()  : null);
        return m;
    }
}
