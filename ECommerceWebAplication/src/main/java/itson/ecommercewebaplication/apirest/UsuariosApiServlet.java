package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet(name = "UsuariosApiServlet", urlPatterns = {"/api/usuarios/*"})
public class UsuariosApiServlet extends HttpServlet {

    private UsuarioBO usuarioBO;

    @Override
    public void init() throws ServletException {
        usuarioBO = new UsuarioBO();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        Usuario usuario = (Usuario) req.getAttribute("usuarioAuth");
        if (usuario == null) { JsonUtil.error(res, 401, "No autenticado."); return; }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.error(res, 400, "Falta el id del usuario en la ruta.");
            return;
        }

        try {
            String[] parts = pathInfo.split("/"); 
            int id = Integer.parseInt(parts[1]);

            if (id != usuario.getId()) {
                JsonUtil.error(res, 403, "Solo puedes editar tu propio perfil.");
                return;
            }

            boolean cambiandoPassword = parts.length >= 3 && "password".equals(parts[2]);
            Map<?, ?> body = JsonUtil.readBody(req, Map.class);

            if (cambiandoPassword) {
                String actual    = (String) body.get("actual");
                String nueva     = (String) body.get("nueva");
                String confirmar = (String) body.get("confirmar");
                usuarioBO.cambiarContrasena(id, actual, nueva, confirmar);
                JsonUtil.ok(res, Map.of(
                    "success", true,
                    "message", "Contraseña actualizada correctamente."
                ));
                return;
            }

            String nombre   = (String) body.get("nombre");
            String telefono = (String) body.get("telefono");
            Usuario actualizado = usuarioBO.actualizarPerfil(id, nombre, telefono);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id",       actualizado.getId());
            data.put("nombre",   actualizado.getNombre());
            data.put("correo",   actualizado.getCorreo());
            data.put("telefono", actualizado.getTelefono());
            data.put("rol",      actualizado.getRol().name());
            JsonUtil.ok(res, data);
        } catch (NumberFormatException e) {
            JsonUtil.error(res, 400, "ID inválido.");
        } catch (Exception e) {
            JsonUtil.error(res, 400, e.getMessage());
        }
    }
}
