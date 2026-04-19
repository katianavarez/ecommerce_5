package itson.ecommercewebaplication.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 * @author PC
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
