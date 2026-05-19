package itson.ecommercewebaplication.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro que exige sesión de cliente en las páginas que no tienen sentido
 * para un visitante anónimo: su cuenta, el checkout y la confirmación de
 * pedido. Si no hay sesión, redirige al login conservando la URL destino
 * en el parámetro {@code redirect}, para devolver al cliente a donde
 * quería ir una vez se autentique.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebFilter(filterName = "ClienteAuthFilter", urlPatterns = {"/app/cuenta", "/app/checkout", "/app/confirmacion"})
public class ClienteAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("clienteLogueado") == null) {
            res.sendRedirect(req.getContextPath() + "/auth/login?redirect=" + req.getRequestURI());
            return;
        }

        chain.doFilter(request, response);
    }
}
