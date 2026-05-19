package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.dto.ResponseDTO;
import itson.ecommercewebaplication.util.JSONMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Endpoint REST de cierre de sesión: POST /api/auth/logout. Invalida la
 * sesión del servlet si existe y responde con un JSON de éxito. El JWT en sí
 * no se "revoca" (es stateless y caduca solo); del lado del cliente, el JS
 * descarta el token guardado en localStorage al llamar a este endpoint.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "AuthLogoutApiServlet", urlPatterns = {"/api/auth/logout"})
public class AuthLogoutApiServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        JSONMapper.mapper.writeValue(res.getWriter(),
                new ResponseDTO(true, "Logout exitoso"));
    }
}
