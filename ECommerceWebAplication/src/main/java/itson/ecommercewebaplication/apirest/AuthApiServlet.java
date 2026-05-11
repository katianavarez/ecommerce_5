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
 *
 * @author
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
            session.setMaxInactiveInterval(30 * 60);
            if (usuario.getRol() == Rol.ADMINISTRADOR) {
                session.setAttribute("usuarioLogueado", usuario);
                session.setAttribute("esAdmin", true);
                session.setAttribute("usuarioId", usuario.getId());
                session.setAttribute("usuarioNombre", usuario.getNombre());
            } else {
                session.setAttribute("clienteLogueado", usuario);
                session.setAttribute("clienteId", usuario.getId());
                session.setAttribute("clienteNombre", usuario.getNombre());
                if (session.getAttribute("carrito") == null) {
                    List<DetallePedido> carritoEnBD = carritoBO.recuperarItemsDesdeDB(usuario.getId());
                    if (carritoEnBD != null && !carritoEnBD.isEmpty()) {
                        session.setAttribute("carrito", carritoEnBD);
                    }
                }
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("token", token);
            data.put("usuarioId", usuario.getId());
            data.put("nombre", usuario.getNombre());
            data.put("rol", usuario.getRol().name());
            JSONMapper.mapper.writeValue(res.getWriter(),
                    new ResponseDTO(true, "Login exitoso", data));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, e.getMessage()));
        }
    }
}
