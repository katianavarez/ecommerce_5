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

/**
 * Endpoint REST del perfil del cliente autenticado. GET /api/usuarios/{id}
 * devuelve los datos del perfil y PUT /api/usuarios/{id} los actualiza;
 * PUT /api/usuarios/{id}/password cambia la contraseña. En todos los casos
 * solo se permite operar sobre el propio usuario (responde 403 si el id de
 * la ruta no coincide con el del token). Tras editar el perfil, sincroniza
 * la sesión para que las páginas renderizadas por el servidor reflejen el
 * cambio sin necesidad de volver a iniciar sesión.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "UsuariosApiServlet", urlPatterns = {"/api/usuarios/*"})
public class UsuariosApiServlet extends HttpServlet {

    private UsuarioBO usuarioBO;

    @Override
    public void init() throws ServletException {
        usuarioBO = new UsuarioBO();
    }

    /**
     * GET /api/usuarios/{id} — devuelve el perfil del usuario autenticado.
     * Solo se permite consultar el propio perfil.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Usuario usuario = (Usuario) req.getAttribute("usuarioAuth");
        if (usuario == null) { JsonUtil.error(res, 401, "No autenticado."); return; }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.error(res, 400, "Falta el id del usuario en la ruta: GET /api/usuarios/{id}");
            return;
        }
        try {
            int id = Integer.parseInt(pathInfo.substring(1).split("/")[0]);
            if (id != usuario.getId()) {
                JsonUtil.error(res, 403, "Solo puedes consultar tu propio perfil.");
                return;
            }
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id",       usuario.getId());
            data.put("nombre",   usuario.getNombre());
            data.put("correo",   usuario.getCorreo());
            data.put("telefono", usuario.getTelefono());
            data.put("rol",      usuario.getRol().name());
            data.put("activo",   usuario.isActivo());
            if (usuario.getDireccion() != null) {
                Map<String, Object> dir = new LinkedHashMap<>();
                dir.put("calle",        usuario.getDireccion().getCalle());
                dir.put("ciudad",       usuario.getDireccion().getCiudad());
                dir.put("estado",       usuario.getDireccion().getEstado());
                dir.put("codigoPostal", usuario.getDireccion().getCodigoPostal());
                data.put("direccion", dir);
            }
            JsonUtil.ok(res, data);
        } catch (NumberFormatException e) {
            JsonUtil.error(res, 400, "ID inválido.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.error(res, 500, "Error interno del servidor.");
        }
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

            // Sincronizar session.clienteLogueado para que las JSPs renderizadas
            // por el servlet (cuenta.jsp, header en otras vistas) reflejen los
            // cambios sin tener que volver a hacer login.
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("clienteLogueado") != null) {
                session.setAttribute("clienteLogueado", actualizado);
                session.setAttribute("clienteNombre", actualizado.getNombre());
            }

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
