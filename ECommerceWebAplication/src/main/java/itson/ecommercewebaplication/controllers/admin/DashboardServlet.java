package itson.ecommercewebaplication.controllers.admin;

import itson.ecommercewebaplication.bo.*;
import itson.ecommercewebaplication.enums.EstadoPedido;
import itson.ecommercewebaplication.models.Pedido;
import itson.ecommercewebaplication.models.Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controlador de la pantalla de inicio del panel admin. Reúne las métricas
 * que se muestran de un vistazo: ventas del mes, número de clientes, total
 * de pedidos y pendientes, los últimos pedidos, los productos más vendidos
 * y el conteo de productos por categoría. Los totales se calculan con
 * consultas COUNT en vez de traer listas enteras a memoria. Ante cualquier
 * fallo deja valores vacíos para que la vista no se rompa.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
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
            // Los conteos se hacen vía BO/DAO (COUNT en JPQL) en vez de traer todo a memoria.
            long totalPedidos = pedidoBO.contarPorEstado("TODOS");
            long pendientes = pedidoBO.contarPorEstado(EstadoPedido.PENDIENTE.name());
            long totalUsuarios = usuarioBO.contarClientes();

            List<Pedido> pedidos = pedidoBO.obtenerTodos();
            // Copiamos a un ArrayList para evitar que la vista subList caduque cuando
            // el EntityManager se cierre y la JSP itere atributos lazy.
            List<Pedido> ultimosPedidos = new ArrayList<>(
                    pedidos.subList(0, Math.min(5, pedidos.size())));

            List<Integer> topIds = pedidoBO.idsProductosTopVentas(4);
            List<Producto> topProductos = productoBO.obtenerPorIds(topIds);

            req.setAttribute("ventasDelMes", pedidoBO.ventasDelMes());
            req.setAttribute("totalUsuarios", totalUsuarios);
            req.setAttribute("totalPedidos", totalPedidos);
            req.setAttribute("pedidosPendientes", pendientes);
            req.setAttribute("ultimosPedidos", ultimosPedidos);
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
