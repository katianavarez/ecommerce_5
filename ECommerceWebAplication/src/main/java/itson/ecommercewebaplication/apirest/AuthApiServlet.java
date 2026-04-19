package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.dto.LoginRequestDTO;
import itson.ecommercewebaplication.dto.ResponseDTO;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JSONMapper;
import itson.ecommercewebaplication.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author PC
 */
@WebServlet(name = "AuthApiServlet", urlPatterns = {"/api/auth/login"})
public class AuthApiServlet extends HttpServlet {

    private UsuarioBO usuarioBO;

    @Override
    public void init() throws ServletException {
        usuarioBO = new UsuarioBO();
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
            JSONMapper.mapper.writeValue(res.getWriter(),
                    new ResponseDTO(true, "Login exitoso", Map.of("token", token, "nombre", usuario.getNombre(), "rol", usuario.getRol().name())));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, e.getMessage()));
        }
    }
}
