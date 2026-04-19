package itson.ecommercewebaplication.controllers.admin;

import itson.ecommercewebaplication.bo.*;
import itson.ecommercewebaplication.enums.EstadoPedido;
import itson.ecommercewebaplication.models.Pedido;
import itson.ecommercewebaplication.models.Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author PC
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/admin/dashboard"})
public class DashboardServlet extends HttpServlet {

    private ProductoBO productoBO;
    private UsuarioBO usuarioBO;
    private PedidoBO pedidoBO;
    private CategoriaBO categoriaBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        usuarioBO = new UsuarioBO();
        pedidoBO = new PedidoBO();
        categoriaBO = new CategoriaBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            List<Pedido> pedidos = pedidoBO.obtenerTodos();
            long pendientes = pedidos.stream()
                    .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE).count();

            List<Integer> topIds = pedidoBO.idsProductosTopVentas(4);
            List<Producto> topProductos = productoBO.obtenerPorIds(topIds);

            req.setAttribute("ventasDelMes", pedidoBO.ventasDelMes());
            req.setAttribute("totalUsuarios", usuarioBO.obtenerTodos().stream()
                    .filter(u -> u.getRol() == itson.ecommercewebaplication.enums.Rol.CLIENTE)
                    .count());
            req.setAttribute("totalPedidos", pedidos.size());
            req.setAttribute("pedidosPendientes", pendientes);
            req.setAttribute("ultimosPedidos", pedidos.subList(0, Math.min(5, pedidos.size())));
            req.setAttribute("topProductos", topProductos);
            req.setAttribute("conteoCategorias", categoriaBO.contarProductosPorCategoria());
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo cargar la información del dashboard.");
            req.setAttribute("ultimosPedidos", Collections.emptyList());
            req.setAttribute("topProductos", Collections.emptyList());
            req.setAttribute("ventasDelMes", 0.0);
        }
        req.getRequestDispatcher("/views/admin/admin-dashboard.jsp").forward(req, res);
    }
}
