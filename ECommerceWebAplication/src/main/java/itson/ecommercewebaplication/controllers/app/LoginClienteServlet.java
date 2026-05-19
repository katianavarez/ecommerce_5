package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.models.DetallePedido;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Controlador del login del cliente. Valida las credenciales, rota el
 * JSESSIONID para mitigar session fixation y deja en la sesión tanto los
 * datos del cliente como un JWT (para que el mismo usuario que entró por el
 * formulario pueda luego consumir la API REST). Además fusiona el carrito
 * que el cliente tuviera como invitado con el que ya tuviera guardado, y
 * valida el parámetro {@code redirect} para evitar open redirects.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "LoginClienteServlet", urlPatterns = {"/auth/login"})
public class LoginClienteServlet extends HttpServlet {

    private UsuarioBO usuarioBO;
    private CarritoBO carritoBO;

    @Override
    public void init() throws ServletException {
        usuarioBO = new UsuarioBO();
        carritoBO = new CarritoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("clienteLogueado") != null) {
            res.sendRedirect(req.getContextPath() + "/app/productos");
            return;
        }
        req.getRequestDispatcher("/views/auth/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String correo = req.getParameter("correo");
        String contrasena = req.getParameter("contrasena");
        String redirect = req.getParameter("redirect");

        try {
            Usuario usuario = usuarioBO.login(correo, contrasena);
            // Rotar JSESSIONID antes de poblar la sesión para mitigar session fixation.
            HttpSession session = req.getSession(true);
            req.changeSessionId();

            session.setAttribute("clienteLogueado", usuario);
            session.setAttribute("clienteId", usuario.getId());
            session.setAttribute("clienteNombre", usuario.getNombre());
            session.setMaxInactiveInterval(30 * 60);

            // Generamos JWT y lo dejamos en la sesión para que las JSP autenticadas
            // lo expongan en un <meta> y el JS lo hidrate en localStorage.
            // Así el cliente que llegó por el servlet también puede consumir la API REST.
            String jwt = JWTUtil.generarToken(usuario.getCorreo(), usuario.getRol().name());
            session.setAttribute("jwtToken", jwt);
            session.setAttribute("jwtUsuarioId", usuario.getId());
            session.setAttribute("jwtNombre", usuario.getNombre());
            session.setAttribute("jwtRol", usuario.getRol().name());

            // Recuperar carrito persistido de BD
            List<DetallePedido> carritoEnBD = carritoBO.recuperarItemsDesdeDB(usuario.getId());

            // Si había algo en sesión antes de loguearse (guest cart), fusionarlo
            @SuppressWarnings("unchecked")
            List<DetallePedido> carritoSesion
                    = (List<DetallePedido>) session.getAttribute("carrito");

            List<DetallePedido> carritoFinal = CarritoBO.fusionar(carritoEnBD, carritoSesion);

            if (carritoSesion != null && !carritoSesion.isEmpty()) {
                try {
                    carritoBO.persistirCarrito(usuario, carritoFinal);
                } catch (Exception ignored) {
                }
            }

            session.setAttribute("carrito", carritoFinal);

            if (redirect != null && !redirect.isBlank()) {
                String decoded = redirect.replace("%3F", "?");
                String ctx = req.getContextPath();
                // Validar para evitar open redirect: solo aceptamos paths internos.
                // Rechazamos URLs absolutas (http://, //evil.com) y backslashes.
                boolean seguro = decoded.startsWith("/")
                        && !decoded.startsWith("//")
                        && !decoded.startsWith("/\\")
                        && !decoded.contains(":");
                if (seguro) {
                    String dest = decoded.startsWith(ctx) ? decoded : ctx + decoded;
                    res.sendRedirect(dest);
                } else {
                    res.sendRedirect(req.getContextPath() + "/app/productos");
                }
            } else {
                res.sendRedirect(req.getContextPath() + "/app/productos");
            }

        } catch (Exception e) {
            // Log detallado para el admin/desarrollador (no expuesto al cliente).
            System.err.println("[LoginCliente] Login fallido para correo=" + correo
                    + " motivo=" + e.getMessage());
            // Mensaje uniforme para no permitir enumeración de cuentas ni revelar
            // estado de la cuenta (activa/inactiva).
            req.setAttribute("error", "Credenciales inválidas.");
            req.setAttribute("correo", correo);
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, res);
        }
    }

}
