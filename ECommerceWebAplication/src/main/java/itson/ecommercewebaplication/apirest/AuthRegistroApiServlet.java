package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.dto.ResponseDTO;
import itson.ecommercewebaplication.enums.Rol;
import itson.ecommercewebaplication.models.Direccion;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JSONMapper;
import itson.ecommercewebaplication.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet(name = "AuthRegistroApiServlet", urlPatterns = {"/api/auth/registro"})
public class AuthRegistroApiServlet extends HttpServlet {

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
            @SuppressWarnings("unchecked")
            Map<String, Object> body = JSONMapper.mapper.readValue(req.getInputStream(), Map.class);

            Usuario usuario = new Usuario();
            usuario.setNombre(asString(body.get("nombre")));
            usuario.setCorreo(asString(body.get("correo")));
            usuario.setContraseña(asString(body.get("contrasena")));
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
            session.setMaxInactiveInterval(30 * 60);
            session.setAttribute("clienteLogueado", registrado);
            session.setAttribute("clienteId", registrado.getId());
            session.setAttribute("clienteNombre", registrado.getNombre());

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("token", token);
            data.put("usuarioId", registrado.getId());
            data.put("nombre", registrado.getNombre());
            data.put("rol", registrado.getRol().name());

            res.setStatus(HttpServletResponse.SC_CREATED);
            JSONMapper.mapper.writeValue(res.getWriter(),
                    new ResponseDTO(true, "Cuenta creada exitosamente", data));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, e.getMessage()));
        }
    }

    private static String asString(Object v) {
        return v == null ? null : v.toString();
    }
}
