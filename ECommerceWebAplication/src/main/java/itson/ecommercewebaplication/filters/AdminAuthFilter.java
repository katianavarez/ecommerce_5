package itson.ecommercewebaplication.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro que protege todo el panel de administración. Antes de dejar pasar
 * cualquier petición a {@code /admin/*} o {@code /views/admin/*} comprueba
 * que haya una sesión iniciada y que el usuario tenga el flag de admin; si
 * no, redirige al login. Deja libres la propia página de login y los assets.
 * 
 * Es lo que cumple el requisito del Avance 3 de bloquear el acceso a los
 * módulos de administración cuando no se ha iniciado sesión como admin.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
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
