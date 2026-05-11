package itson.ecommercewebaplication.filters;

import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JWTUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author PC
 */
@WebFilter(filterName = "ApiAuthFilter", urlPatterns = {"/api/*"})
public class ApiAuthFilter implements Filter {

    private final UsuarioBO usuarioBO = new UsuarioBO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        // Endpoints públicos: login, registro y logout (este último idempotente).
        if (uri.endsWith("/api/auth/login")
                || uri.endsWith("/api/auth/registro")
                || uri.endsWith("/api/auth/logout")) {
            chain.doFilter(request, response);
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"success\":false,\"message\":\"Token requerido\"}");
            return;
        }

        try {
            String token = authHeader.substring(7);
            String correo = JWTUtil.validarToken(token);
            String rol = JWTUtil.getRolFromToken(token);

            // Resolver el usuario contra BD para inyectar su id real y validar que sigue activo.
            Usuario usuario = usuarioBO.obtenerPorCorreo(correo);
            if (usuario == null || !usuario.isActivo()) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("{\"success\":false,\"message\":\"Cuenta no disponible\"}");
                return;
            }

            req.setAttribute("correoUsuario", correo);
            req.setAttribute("rolUsuario", rol);
            req.setAttribute("usuarioIdAuth", usuario.getId());
            req.setAttribute("usuarioAuth", usuario);
            chain.doFilter(request, response);
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"success\":false,\"message\":\"Token inválido o expirado\"}");
        }
    }
}
