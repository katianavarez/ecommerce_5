package itson.ecommercewebaplication.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 
 * @author PC
 */
public class JsonUtil {

    public static void ok(HttpServletResponse res, Object data) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(HttpServletResponse.SC_OK);
        JSONMapper.mapper.writeValue(res.getWriter(), data);
    }

    public static void error(HttpServletResponse res, int status, String mensaje) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(status);
        JSONMapper.mapper.writeValue(res.getWriter(),
            Map.of("success", false, "message", mensaje));
    }

    public static <T> T readBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        return JSONMapper.mapper.readValue(req.getInputStream(), clazz);
    }
}
