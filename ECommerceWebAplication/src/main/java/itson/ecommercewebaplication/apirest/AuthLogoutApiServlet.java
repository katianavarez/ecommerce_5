package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.dto.ResponseDTO;
import itson.ecommercewebaplication.util.JSONMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

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
