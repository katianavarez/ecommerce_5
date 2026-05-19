package itson.ecommercewebaplication.filters;

import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.enums.EstadoPedido;
import itson.ecommercewebaplication.models.Pedido;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Filtro auxiliar del panel admin que, en cada petición de un admin
 * autenticado, calcula cuántos pedidos están pendientes y lo deja como
 * atributo del request. Así el badge de "pedidos pendientes" del menú
 * lateral aparece en todas las pantallas sin que cada servlet tenga que
 * recalcularlo. Si algo falla, deja el contador en cero y no rompe la vista.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebFilter(filterName = "AdminDataFilter", urlPatterns = {"/admin/*", "/views/admin/*"})
public class AdminDataFilter implements Filter {

    private PedidoBO pedidoBO;

    @Override
    public void init(FilterConfig config) throws ServletException {
        pedidoBO = new PedidoBO();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        HttpSession session = req.getSession(false);
        boolean esAdmin = session != null
                && Boolean.TRUE.equals(session.getAttribute("esAdmin"));

        if (uri.endsWith("/admin/login") || !esAdmin) {
            chain.doFilter(request, response);
            return;
        }

        try {
            List<Pedido> todos = pedidoBO.obtenerTodos();
            long pendientes = todos.stream()
                    .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE)
                    .count();
            req.setAttribute("pedidosPendientes", pendientes);
        } catch (Exception e) {
            req.setAttribute("pedidosPendientes", 0L);
        }

        chain.doFilter(request, response);
    }
}
