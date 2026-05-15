package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.CategoriaBO;
import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.bo.ProductoBO;
import itson.ecommercewebaplication.models.Categoria;
import itson.ecommercewebaplication.models.Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author PC
 */
@WebServlet(name = "InicioServlet", urlPatterns = {"/", "/inicio"})
public class InicioServlet extends HttpServlet {

    private ProductoBO productoBO;
    private CategoriaBO categoriaBO;
    private PedidoBO pedidoBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        categoriaBO = new CategoriaBO();
        pedidoBO = new PedidoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            List<Categoria> categorias = categoriaBO.obtenerTodas();
            Map<Integer, Long> conteos = categoriaBO.contarProductosPorCategoria();

            // Top 4 productos más vendidos; fallback: los 4 más recientes
            List<Integer> topIds = pedidoBO.idsProductosTopVentas(4);
            List<Producto> topProductos = topIds.isEmpty()
                    ? productoBO.obtenerPaginados(1, 4)
                    : productoBO.obtenerPorIds(topIds);

            req.setAttribute("categorias", categorias);
            req.setAttribute("conteoCategorias", conteos);
            req.setAttribute("topProductos", topProductos);
        } catch (Exception e) {
            req.setAttribute("categorias", new ArrayList<>());
            req.setAttribute("topProductos", new ArrayList<>());
        }
        req.getRequestDispatcher("/index.jsp").forward(req, res);
    }
}
