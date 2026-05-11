package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.dto.ResponseDTO;
import itson.ecommercewebaplication.models.DetallePedido;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JSONMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AuthLogoutApiServlet", urlPatterns = {"/api/auth/logout"})
public class AuthLogoutApiServlet extends HttpServlet {

    private CarritoBO carritoBO;

    @Override
    public void init() throws ServletException {
        carritoBO = new CarritoBO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session != null) {
            try {
                Usuario usuario = (Usuario) session.getAttribute("clienteLogueado");
                @SuppressWarnings("unchecked")
                List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
                if (usuario != null) {
                    carritoBO.persistirCarrito(usuario, carrito);
                }
            } catch (Exception ignored) {
            }
            session.invalidate();
        }

        JSONMapper.mapper.writeValue(res.getWriter(),
                new ResponseDTO(true, "Logout exitoso"));
    }
}
