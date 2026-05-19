package itson.ecommercewebaplication.filters;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Bloquea el acceso directo (dispatcher REQUEST) a /views/* para que los JSPs
 * solo se rendericen vía forward desde un Servlet. Los forwards internos no
 * pasan por este filtro porque no estamos registrando DispatcherType.FORWARD.
 * 
 * De esta forma evitamos que alguien teclee la ruta de un JSP en el navegador
 * y vea la vista sin pasar por el controlador (que es quien prepara los datos
 * y aplica la autenticación), respondiendo 404 en ese caso.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebFilter(filterName = "BlockDirectViewsFilter",
        urlPatterns = {"/views/*"},
        dispatcherTypes = {DispatcherType.REQUEST})
public class BlockDirectViewsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
