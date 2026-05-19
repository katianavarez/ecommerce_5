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
 * Endpoint REST de reseñas. GET /api/resenas/producto/{id} es público y
 * devuelve las reseñas de un producto junto con su promedio y total; POST
 * /api/resenas requiere JWT y crea una reseña, pero antes valida dos reglas
 * de negocio: que el cliente haya comprado el producto y que no haya dejado
 * ya una reseña (responde 403 o 409 respectivamente).
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
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
            Map<?, ?> body       = JsonUtil.readBody(req, Map.class);
            int productoId       = asInt(body.get("productoId"));
            int calificacion     = asInt(body.get("calificacion"));
            String comentario    = body.get("comentario") != null ? body.get("comentario").toString() : null;

            if (calificacion < 1 || calificacion > 5) {
                JsonUtil.error(res, 400, "calificacion debe estar entre 1 y 5."); return;
            }

            Producto producto = productoBO.obtenerPorId(productoId);
            if (producto == null) { JsonUtil.error(res, 404, "Producto no encontrado."); return; }

            int usuarioId = usuario.getId();
            if (!pedidoBO.usuarioComproProducto(usuarioId, productoId)) {
                JsonUtil.error(res, 403, "Solo puedes reseñar productos que hayas comprado."); return;
            }

            // Política: 1 reseña por producto por usuario (estándar e-commerce).
            // El check anterior permitía N reseñas si había N compras, lo cual era
            // poco intuitivo y poco común en plataformas reales.
            long dadas = resenaBO.contarResenasPorUsuarioYProducto(usuarioId, productoId);
            if (dadas > 0) {
                JsonUtil.error(res, 409, "Ya has dejado una reseña para este producto.");
                return;
            }

            Resenia nueva = new Resenia(calificacion, comentario, LocalDate.now(), producto, usuario);
            Resenia creada = resenaBO.crear(nueva);

            JsonUtil.created(res, toMap(creada));

        } catch (IllegalArgumentException | NullPointerException | ClassCastException e) {
            JsonUtil.error(res, 400, "Faltan campos o tipos inválidos: productoId (int), calificacion (int 1-5).");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.error(res, 500, "Error interno del servidor.");
        }
    }

    private static int asInt(Object v) {
        if (v == null) throw new IllegalArgumentException("valor requerido");
        if (v instanceof Number) return ((Number) v).intValue();
        return Integer.parseInt(v.toString().trim());
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
