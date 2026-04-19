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
@WebFilter(filterName = "AdminAuthFilter", urlPatterns = {"/admin/*", "/views/admin/*"})
public class AdminAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        if (uri.endsWith("/admin/login") || uri.contains("/assets/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            res.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }

        Boolean esAdmin = (Boolean) session.getAttribute("esAdmin");
        if (esAdmin == null || !esAdmin) {
            res.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }

        chain.doFilter(request, response);
    }
}
