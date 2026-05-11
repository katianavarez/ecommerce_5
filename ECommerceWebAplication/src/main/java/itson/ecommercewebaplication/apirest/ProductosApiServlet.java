package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.ProductoBO;
import itson.ecommercewebaplication.models.Producto;
import itson.ecommercewebaplication.models.StockTalla;
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
@WebServlet(name = "ProductosApiServlet", urlPatterns = {"/api/productos", "/api/productos/*"})
public class ProductosApiServlet extends HttpServlet {

    private ProductoBO productoBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.length() > 1) {
                int id = Integer.parseInt(pathInfo.substring(1));
                Producto p = productoBO.obtenerPorId(id);
                if (p == null) {
                    JsonUtil.error(res, 404, "Producto no encontrado.");
                    return;
                }
                JsonUtil.ok(res, toMap(p));
            } else {
                String busqueda = req.getParameter("busqueda");
                String[] catParams = req.getParameterValues("categoriaId");
                String[] tallaParams = req.getParameterValues("talla");
                String colorP = req.getParameter("color");
                String precioMaxP = req.getParameter("precioMax");
                String soloStockP = req.getParameter("soloConStock");
                String pageP = req.getParameter("page");
                String sizeP = req.getParameter("size");

                int page = pageP != null ? Math.max(1, Integer.parseInt(pageP)) : 1;
                int size = sizeP != null ? Math.max(1, Integer.parseInt(sizeP)) : 12;

                List<Integer> categoriaIds = new ArrayList<>();
                if (catParams != null) {
                    for (String c : catParams) {
                        if (c == null || c.isBlank()) {
                            continue;
                        }
                        try {
                            categoriaIds.add(Integer.parseInt(c));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
                List<String> tallasSel = new ArrayList<>();
                if (tallaParams != null) {
                    for (String t : tallaParams) {
                        if (t != null && !t.isBlank()) {
                            tallasSel.add(t);
                        }
                    }
                }
                Double precioMax = null;
                if (precioMaxP != null && !precioMaxP.isBlank()) {
                    try {
                        precioMax = Double.parseDouble(precioMaxP);
                    } catch (NumberFormatException ignored) {
                    }
                }
                boolean soloConStock = "true".equalsIgnoreCase(soloStockP);
                String color = (colorP != null && !colorP.isBlank()) ? colorP : null;

                boolean usaFiltros = !categoriaIds.isEmpty() || !tallasSel.isEmpty()
                        || color != null || precioMax != null || soloConStock;

                List<Producto> productos;
                long total;

                if (busqueda != null && !busqueda.isBlank()) {
                    productos = productoBO.buscarPorNombre(busqueda);
                    total = productos.size();
                } else if (usaFiltros) {
                    productos = productoBO.filtrar(
                            categoriaIds.isEmpty() ? null : categoriaIds,
                            tallasSel.isEmpty() ? null : tallasSel,
                            color, null, precioMax, soloConStock);
                    total = productos.size();
                } else {
                    total = productoBO.contarProductos();
                    productos = productoBO.obtenerPaginados(page, size);
                }

                if ((busqueda != null && !busqueda.isBlank()) || usaFiltros) {
                    int desde = Math.max(0, (page - 1) * size);
                    int hasta = Math.min(desde + size, productos.size());
                    productos = desde >= productos.size()
                            ? new ArrayList<>()
                            : new ArrayList<>(productos.subList(desde, hasta));
                }

                int totalPaginas = total == 0 ? 1 : (int) Math.ceil((double) total / size);
                List<Map<String, Object>> items = new ArrayList<>();
                for (Producto p : productos) {
                    items.add(toMapSimple(p));
                }

                Map<String, Object> respuesta = new LinkedHashMap<>();
                respuesta.put("page", page);
                respuesta.put("size", size);
                respuesta.put("total", total);
                respuesta.put("totalPaginas", totalPaginas);
                respuesta.put("productos", items);
                JsonUtil.ok(res, respuesta);
            }
        } catch (NumberFormatException e) {
            JsonUtil.error(res, 400, "ID o parámetro numérico inválido.");
        } catch (Exception e) {
            JsonUtil.error(res, 500, "Error interno: " + e.getMessage());
        }
    }

    private Map<String, Object> toMap(Producto p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("nombre", p.getNombre());
        m.put("descripcion", p.getDescripcion());
        m.put("precio", p.getPrecio());
        m.put("imagenURL", p.getImagenURL());
        m.put("stock", p.getStock());
        m.put("color", p.getColor());
        m.put("activo", p.isActivo());
        m.put("categoria", p.getCategoria() != null
                ? Map.of("id", p.getCategoria().getId(), "nombre", p.getCategoria().getNombre())
                : null);
        m.put("proveedor", p.getProveedor() != null
                ? Map.of("id", p.getProveedor().getId(), "nombre", p.getProveedor().getNombre())
                : null);
        List<String> tallas = new ArrayList<>();
        if (p.getTallasDisponibles() != null) {
            for (var t : p.getTallasDisponibles()) {
                tallas.add(t.name());
            }
        }
        m.put("tallasDisponibles", tallas);
        List<Map<String, Object>> stockTalla = new ArrayList<>();
        if (p.getStockPorTalla() != null) {
            for (StockTalla st : p.getStockPorTalla()) {
                stockTalla.add(Map.of("talla", st.getTalla().name(), "cantidad", st.getCantidad()));
            }
        }
        m.put("stockPorTalla", stockTalla);
        return m;
    }

    private Map<String, Object> toMapSimple(Producto p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("nombre", p.getNombre());
        m.put("precio", p.getPrecio());
        m.put("imagenURL", p.getImagenURL());
        m.put("stock", p.getStock());
        m.put("color", p.getColor());
        m.put("categoria", p.getCategoria() != null ? p.getCategoria().getNombre() : null);
        return m;
    }
}
