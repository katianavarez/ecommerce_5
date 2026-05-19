package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.dto.LoginRequestDTO;
import itson.ecommercewebaplication.dto.ResponseDTO;
import itson.ecommercewebaplication.enums.Rol;
import itson.ecommercewebaplication.models.DetallePedido;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JSONMapper;
import itson.ecommercewebaplication.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoint REST de inicio de sesión: POST /api/auth/login. Valida las
 * credenciales, genera el JWT que el cliente usará en las siguientes
 * peticiones y, en paralelo, prepara la sesión del servlet (datos de
 * cliente o admin según el rol) para que ambos flujos queden alineados.
 * Para clientes, además fusiona el carrito de invitado con el de BD.
 * Devuelve 401 ante credenciales inválidas y 500 solo ante errores de
 * infraestructura, sin filtrar el stacktrace.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "AuthApiServlet", urlPatterns = {"/api/auth/login"})
public class AuthApiServlet extends HttpServlet {

    private UsuarioBO usuarioBO;
    private CarritoBO carritoBO;

    @Override
    public void init() throws ServletException {
        usuarioBO = new UsuarioBO();
        carritoBO = new CarritoBO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            LoginRequestDTO loginReq = JSONMapper.mapper.readValue(req.getInputStream(), LoginRequestDTO.class);
            Usuario usuario = usuarioBO.login(loginReq.getCorreo(), loginReq.getContrasena());
            String token = JWTUtil.generarToken(usuario.getCorreo(), usuario.getRol().name());
            HttpSession session = req.getSession(true);
            // Rotar JSESSIONID para evitar session fixation.
            req.changeSessionId();
            session.setMaxInactiveInterval(30 * 60);
            // También exponemos el JWT en la sesión para que JSPs lo lean si el
            // usuario navega luego por las páginas servlet-rendered.
            session.setAttribute("jwtToken", token);
            session.setAttribute("jwtUsuarioId", usuario.getId());
            session.setAttribute("jwtNombre", usuario.getNombre());
            session.setAttribute("jwtRol", usuario.getRol().name());
            if (usuario.getRol() == Rol.ADMINISTRADOR) {
                session.setAttribute("usuarioLogueado", usuario);
                session.setAttribute("esAdmin", true);
                session.setAttribute("usuarioId", usuario.getId());
                session.setAttribute("usuarioNombre", usuario.getNombre());
            } else {
                session.setAttribute("clienteLogueado", usuario);
                session.setAttribute("clienteId", usuario.getId());
                session.setAttribute("clienteNombre", usuario.getNombre());

                List<DetallePedido> carritoEnBD = carritoBO.recuperarItemsDesdeDB(usuario.getId());
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
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("token", token);
            data.put("usuarioId", usuario.getId());
            data.put("nombre", usuario.getNombre());
            data.put("rol", usuario.getRol().name());
            JSONMapper.mapper.writeValue(res.getWriter(),
                    new ResponseDTO(true, "Login exitoso", data));
        } catch (Exception e) {
            // UsuarioBO.login lanza Exception con mensajes específicos de auth.
            // Cualquier otro tipo de error es de infraestructura → 500 sin leakear stacktrace.
            String msg = e.getMessage();
            boolean esAuth = msg != null
                    && (msg.contains("Credenciales") || msg.contains("dada de baja"));
            if (esAuth) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, msg));
            } else {
                System.err.println("[AuthApi] Error interno en login: " + msg);
                e.printStackTrace();
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JSONMapper.mapper.writeValue(res.getWriter(),
                        new ResponseDTO(false, "Error interno al procesar el login."));
            }
        }
    }
}
