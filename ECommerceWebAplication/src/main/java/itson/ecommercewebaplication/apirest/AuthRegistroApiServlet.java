package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.dto.ResponseDTO;
import itson.ecommercewebaplication.enums.Rol;
import itson.ecommercewebaplication.models.DetallePedido;
import itson.ecommercewebaplication.models.Direccion;
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
 * Endpoint REST de registro: POST /api/auth/registro. Crea la cuenta de
 * cliente con su dirección, devuelve 201 con el JWT ya generado y deja al
 * usuario logueado en sesión para que pueda comprar de inmediato. Distingue
 * los errores de validación del BO (responde 400 con el mensaje) de los
 * errores técnicos de BD/Hibernate (responde 500 con un mensaje genérico),
 * para no exponer detalles internos al cliente.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "AuthRegistroApiServlet", urlPatterns = {"/api/auth/registro"})
public class AuthRegistroApiServlet extends HttpServlet {

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
            @SuppressWarnings("unchecked")
            Map<String, Object> body = JSONMapper.mapper.readValue(req.getInputStream(), Map.class);

            Usuario usuario = new Usuario();
            usuario.setNombre(asString(body.get("nombre")));
            usuario.setCorreo(asString(body.get("correo")));
            usuario.setContrasena(asString(body.get("contrasena")));
            usuario.setTelefono(asString(body.get("telefono")));
            usuario.setRol(Rol.CLIENTE);

            Direccion direccion = new Direccion(
                    asString(body.get("calle")),
                    asString(body.get("ciudad")),
                    asString(body.get("estado")),
                    asString(body.get("codigoPostal"))
            );

            Usuario registrado = usuarioBO.registrar(usuario, direccion);
            String token = JWTUtil.generarToken(registrado.getCorreo(), registrado.getRol().name());

            HttpSession session = req.getSession(true);
            // Rotar JSESSIONID para evitar session fixation tras el alta.
            req.changeSessionId();
            session.setMaxInactiveInterval(30 * 60);
            session.setAttribute("clienteLogueado", registrado);
            session.setAttribute("clienteId", registrado.getId());
            session.setAttribute("clienteNombre", registrado.getNombre());
            session.setAttribute("jwtToken", token);
            session.setAttribute("jwtUsuarioId", registrado.getId());
            session.setAttribute("jwtNombre", registrado.getNombre());
            session.setAttribute("jwtRol", registrado.getRol().name());

            @SuppressWarnings("unchecked")
            List<DetallePedido> carritoSesion
                    = (List<DetallePedido>) session.getAttribute("carrito");
            if (carritoSesion != null && !carritoSesion.isEmpty()) {
                try {
                    carritoBO.persistirCarrito(registrado, carritoSesion);
                } catch (Exception ignored) {
                }
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("token", token);
            data.put("usuarioId", registrado.getId());
            data.put("nombre", registrado.getNombre());
            data.put("rol", registrado.getRol().name());

            res.setStatus(HttpServletResponse.SC_CREATED);
            JSONMapper.mapper.writeValue(res.getWriter(),
                    new ResponseDTO(true, "Cuenta creada exitosamente", data));
        } catch (Exception e) {
            // Filtramos mensajes técnicos (Hibernate, SQL) para no exponer detalles
            // internos al cliente; los errores de validación del BO sí los pasamos.
            String msg = e.getMessage();
            String low = msg == null ? "" : msg.toLowerCase();
            boolean tecnico = msg == null
                    || msg.length() > 200
                    || low.contains("hibernate")
                    || low.contains("sql")
                    || low.contains("constraint")
                    || low.contains("could not")
                    || low.contains("nested exception");
            if (tecnico) {
                e.printStackTrace();
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JSONMapper.mapper.writeValue(res.getWriter(),
                        new ResponseDTO(false, "No se pudo completar el registro. Inténtalo más tarde."));
            } else {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, msg));
            }
        }
    }

    private static String asString(Object v) {
        return v == null ? null : v.toString();
    }
}
