package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.models.DetallePedido;
import itson.ecommercewebaplication.models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PC
 */
@WebServlet(name = "LoginClienteServlet", urlPatterns = {"/auth/login"})
public class LoginClienteServlet extends HttpServlet {

    private UsuarioBO usuarioBO;
    private CarritoBO carritoBO;

    @Override
    public void init() throws ServletException {
        usuarioBO = new UsuarioBO();
        carritoBO = new CarritoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("clienteLogueado") != null) {
            res.sendRedirect(req.getContextPath() + "/app/productos");
            return;
        }
        req.getRequestDispatcher("/views/auth/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String correo = req.getParameter("correo");
        String contrasena = req.getParameter("contrasena");
        String redirect = req.getParameter("redirect");

        try {
            Usuario usuario = usuarioBO.login(correo, contrasena);
            HttpSession session = req.getSession();

            session.setAttribute("clienteLogueado", usuario);
            session.setAttribute("clienteId", usuario.getId());
            session.setAttribute("clienteNombre", usuario.getNombre());
            session.setMaxInactiveInterval(30 * 60);

            // Recuperar carrito persistido de BD
            List<DetallePedido> carritoEnBD = carritoBO.recuperarItemsDesdeDB(usuario.getId());

            // Si había algo en sesión antes de loguearse (guest cart), fusionarlo
            @SuppressWarnings("unchecked")
            List<DetallePedido> carritoSesion
                    = (List<DetallePedido>) session.getAttribute("carrito");

            List<DetallePedido> carritoFinal = fusionarCarritos(carritoEnBD, carritoSesion);
            session.setAttribute("carrito", carritoFinal);

            if (redirect != null && !redirect.isBlank()) {
                // Decodificar %3F a ? (viene codificado desde el href del JSP)
                String decoded = redirect.replace("%3F", "?");
                // Anteponer contextPath si la ruta no lo incluye ya
                String ctx = req.getContextPath();
                String dest = decoded.startsWith(ctx) ? decoded : ctx + decoded;
                res.sendRedirect(dest);
            } else {
                res.sendRedirect(req.getContextPath() + "/app/productos");
            }

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("correo", correo);
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, res);
        }
    }

    /**
     * Fusiona el carrito de BD con el de sesión (si el usuario agregó algo sin
     * estar logueado). Los items de sesión tienen prioridad en cantidad si el
     * producto ya está en BD.
     */
    private List<DetallePedido> fusionarCarritos(
            List<DetallePedido> deBD, List<DetallePedido> deSesion) {

        if (deBD == null || deBD.isEmpty()) {
            return deSesion != null ? deSesion : new ArrayList<>();
        }
        if (deSesion == null || deSesion.isEmpty()) {
            return deBD;
        }

        // Usar la BD como base y agregar/actualizar con los de sesión
        List<DetallePedido> resultado = new ArrayList<>(deBD);

        for (DetallePedido itemSesion : deSesion) {
            boolean encontrado = false;
            for (DetallePedido itemBD : resultado) {
                if (itemBD.getProducto().getId() == itemSesion.getProducto().getId()) {
                    // Sumar cantidades (el usuario pudo haber agregado más sin loguearse)
                    itemBD.setCantidad(itemBD.getCantidad() + itemSesion.getCantidad());
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                resultado.add(itemSesion);
            }
        }

        return resultado;
    }
}
