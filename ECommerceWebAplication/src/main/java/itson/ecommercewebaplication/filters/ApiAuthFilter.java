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
 * Filtro de autenticación de toda la API REST ({@code /api/*}). Deja pasar
 * sin token los endpoints públicos (login, registro, logout, el catálogo en
 * GET y las reseñas por producto) y exige un JWT válido en el header
 * {@code Authorization: Bearer ...} para el resto. Cuando el token es válido,
 * resuelve el usuario contra BD, comprueba que siga activo y lo deja en los
 * atributos de la request para que cada servlet sepa quién está pidiendo.
 * También responde el preflight CORS (OPTIONS).
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebFilter(filterName = "ApiAuthFilter", urlPatterns = {"/api/*"})
public class ApiAuthFilter implements Filter {

    private final UsuarioBO usuarioBO = new UsuarioBO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo() != null ? req.getPathInfo() : "";
        String method = req.getMethod();

        // CORS preflight: responder con headers explícitos y terminar.
        if ("OPTIONS".equalsIgnoreCase(method)) {
            res.setHeader("Access-Control-Allow-Origin", "*");
            res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            res.setHeader("Access-Control-Max-Age", "3600");
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Endpoints públicos: match exacto sobre servletPath + método (no usar contains)
        // para evitar bypass tipo /api/pedidos?x=/api/productos.
        boolean publicEndpoint = false;
        if ("/api/auth/login".equals(servletPath)
                || "/api/auth/registro".equals(servletPath)
                || "/api/auth/logout".equals(servletPath)) {
            publicEndpoint = true;
        }
        // Catálogo lectura pública: GET /api/productos y /api/productos/{id}
        if ("GET".equalsIgnoreCase(method) && "/api/productos".equals(servletPath)) {
            publicEndpoint = true;
        }
        // Reseñas por producto: GET /api/resenas/producto/{id}
        if ("GET".equalsIgnoreCase(method) && "/api/resenas".equals(servletPath)
                && pathInfo.startsWith("/producto/")) {
            publicEndpoint = true;
        }

        if (publicEndpoint) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
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
                res.setCharacterEncoding("UTF-8");
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
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write("{\"success\":false,\"message\":\"Token inválido o expirado\"}");
        }
    }
}
