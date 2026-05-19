package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.CategoriaBO;
import itson.ecommercewebaplication.bo.ProductoBO;
import itson.ecommercewebaplication.enums.Colores;
import itson.ecommercewebaplication.enums.Tallas;
import itson.ecommercewebaplication.models.Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Controlador del catálogo del cliente. Renderiza la página de productos con
 * su primera tanda de resultados ya pintada desde el servidor (para que se
 * vea contenido aunque el JavaScript no haya cargado todavía); a partir de
 * ahí, productos.js toma el control y rehace la búsqueda y los filtros vía
 * Fetch contra la API REST. Soporta búsqueda por nombre, filtros combinados
 * (categoría, talla, color, precio máximo, solo con stock) y paginación.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "ProductosServlet", urlPatterns = {"/app/productos"})
public class ProductosServlet extends HttpServlet {

    private static final int TAMANO_PAGINA = 6;
    private ProductoBO productoBO;
    private CategoriaBO categoriaBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        categoriaBO = new CategoriaBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String busqueda = req.getParameter("busqueda");
            String paginaStr = req.getParameter("pagina");
            int pagina = (paginaStr != null && !paginaStr.isBlank()) ? Integer.parseInt(paginaStr) : 1;

            // ── Parámetros de filtro ─────────────────────────────
            String[] catParams = req.getParameterValues("categoriaId");
            String[] tallaParams = req.getParameterValues("talla");
            String colorParam = req.getParameter("color");
            String precioMaxP = req.getParameter("precioMax");
            String soloStockP = req.getParameter("soloConStock");

            List<Integer> categoriaIds = new ArrayList<>();
            if (catParams != null) {
                for (String c : catParams)
                    try {
                    categoriaIds.add(Integer.parseInt(c));
                } catch (NumberFormatException ignored) {
                }
            }

            List<String> tallasSelec = (tallaParams != null) ? Arrays.asList(tallaParams) : new ArrayList<>();
            String color = (colorParam != null && !colorParam.isBlank()) ? colorParam : null;
            Double precioMax = null;
            if (precioMaxP != null && !precioMaxP.isBlank())
                try {
                precioMax = Double.parseDouble(precioMaxP);
            } catch (NumberFormatException ignored) {
            }
            boolean soloConStock = "true".equals(soloStockP);

            boolean usaFiltros = !categoriaIds.isEmpty() || !tallasSelec.isEmpty()
                    || color != null || precioMax != null || soloConStock
                    || (busqueda != null && !busqueda.isBlank());

            List<Producto> productos;
            long totalProductos;

            if (busqueda != null && !busqueda.isBlank()) {
                List<Producto> todos = productoBO.buscarPorNombre(busqueda);
                totalProductos = todos.size();
                productos = paginar(todos, pagina, TAMANO_PAGINA);
                req.setAttribute("busqueda", busqueda);
            } else if (usaFiltros) {
                List<Producto> todos = productoBO.filtrar(
                        categoriaIds.isEmpty() ? null : categoriaIds,
                        tallasSelec.isEmpty() ? null : tallasSelec,
                        color, null, precioMax, soloConStock);
                totalProductos = todos.size();
                productos = paginar(todos, pagina, TAMANO_PAGINA);
            } else {
                totalProductos = productoBO.contarProductos();
                productos = productoBO.obtenerPaginados(pagina, TAMANO_PAGINA);
            }

            int totalPaginas = (int) Math.ceil((double) totalProductos / TAMANO_PAGINA);

            // Pasar enums al JSP para los filtros
            req.setAttribute("productos", productos);
            req.setAttribute("categorias", categoriaBO.obtenerTodas());
            req.setAttribute("tallasEnum", Tallas.values());
            req.setAttribute("coloresEnum", Colores.values());
            req.setAttribute("paginaActual", pagina);
            req.setAttribute("totalPaginas", totalPaginas);
            req.setAttribute("totalProductos", totalProductos);
            req.setAttribute("precioMaximo", productoBO.obtenerPrecioMaximo());
            // Devolver filtros activos para mantener el estado
            req.setAttribute("categoriasActivas", categoriaIds);
            req.setAttribute("tallasActivas", tallasSelec);
            req.setAttribute("colorActivo", color);
            req.setAttribute("precioMaxActivo", precioMaxP);
            req.setAttribute("soloConStockActivo", soloConStock);

            req.getRequestDispatcher("/views/aplication/productos.jsp").forward(req, res);

        } catch (Exception e) {
            req.setAttribute("error", "No se pudo cargar el catálogo. Por favor intenta más tarde.");
            req.setAttribute("productos", Collections.emptyList());
            req.setAttribute("categorias", categoriaBO.obtenerTodas());
            req.setAttribute("tallasEnum", Tallas.values());
            req.setAttribute("coloresEnum", Colores.values());
            req.setAttribute("precioMaximo", 0.0);
            req.setAttribute("categoriasActivas", Collections.emptyList());
            req.setAttribute("tallasActivas", Collections.emptyList());
            req.setAttribute("colorActivo", "");
            req.setAttribute("soloConStockActivo", false);
            req.getRequestDispatcher("/views/aplication/productos.jsp").forward(req, res);
        }
    }

    private List<Producto> paginar(List<Producto> todos, int pagina, int tamano) {
        int desde = (pagina - 1) * tamano;
        if (desde >= todos.size()) {
            return Collections.emptyList();
        }
        int hasta = Math.min(desde + tamano, todos.size());
        return todos.subList(desde, hasta);
    }
}
