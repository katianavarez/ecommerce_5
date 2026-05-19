package itson.ecommercewebaplication.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Helpers que usan los servlets de la API REST para escribir respuestas
 * JSON con el código HTTP correcto y leer el body de los requests.
 * Centralizar esto evita tener que repetir {@code setContentType},
 * {@code setStatus} y la lectura del input stream en cada servlet.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class JsonUtil {

    /**
     * Escribe un cuerpo JSON sin sobrescribir el status. Si el caller
     * seteó 201/204 antes, se conserva (default de la response es 200).
     */
    public static void ok(HttpServletResponse res, Object data) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        JSONMapper.mapper.writeValue(res.getWriter(), data);
    }

    /** 201 Created con cuerpo JSON. */
    public static void created(HttpServletResponse res, Object data) throws IOException {
        res.setStatus(HttpServletResponse.SC_CREATED);
        ok(res, data);
    }

    /** 204 No Content (sin cuerpo). */
    public static void noContent(HttpServletResponse res) {
        res.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Responde con el código de error indicado y un cuerpo
     * {@code {success: false, message: ...}} que el JS del cliente
     * usa para mostrar el mensaje al usuario.
     */
    public static void error(HttpServletResponse res, int status, String mensaje) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(status);
        JSONMapper.mapper.writeValue(res.getWriter(),
            Map.of("success", false, "message", mensaje));
    }

    /**
     * Lee el cuerpo de la petición como JSON y lo convierte a la clase
     * indicada. Lanza IOException si el JSON está malformado.
     */
    public static <T> T readBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        return JSONMapper.mapper.readValue(req.getInputStream(), clazz);
    }
}
