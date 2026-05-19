package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.models.Direccion;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Controlador del registro de clientes. El GET muestra el formulario (o
 * redirige si ya hay sesión); el POST arma el usuario y su dirección, los
 * registra a través de {@code UsuarioBO} y, si todo sale bien, deja al
 * cliente ya logueado con su JWT en sesión para que pueda comprar de
 * inmediato. Si la validación falla, vuelve al formulario conservando lo
 * que el usuario ya había escrito.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "RegistroServlet", urlPatterns = {"/auth/registro"})
public class RegistroServlet extends HttpServlet {

    private UsuarioBO usuarioBO;

    @Override
    public void init() throws ServletException {
        usuarioBO = new UsuarioBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("clienteLogueado") != null) {
            res.sendRedirect(req.getContextPath() + "/app/productos");
            return;
        }
        req.getRequestDispatcher("/views/auth/registro.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String nombre = req.getParameter("nombre");
        String correo = req.getParameter("correo");
        String contrasena = req.getParameter("contrasena");
        String telefono = req.getParameter("telefono");
        String calle = req.getParameter("calle");
        String ciudad = req.getParameter("ciudad");
        String estado = req.getParameter("estado");
        String codigoPostal = req.getParameter("codigoPostal");
        try {
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setCorreo(correo);
            usuario.setContrasena(contrasena);
            usuario.setTelefono(telefono);
            Direccion direccion = new Direccion(calle, ciudad, estado, codigoPostal);
            Usuario registrado = usuarioBO.registrar(usuario, direccion);

            // Auto-login después del registro para mejor UX y para que cuente
            // con JWT en la sesión inmediatamente.
            HttpSession session = req.getSession(true);
            req.changeSessionId();
            session.setMaxInactiveInterval(30 * 60);
            session.setAttribute("clienteLogueado", registrado);
            session.setAttribute("clienteId", registrado.getId());
            session.setAttribute("clienteNombre", registrado.getNombre());

            String jwt = JWTUtil.generarToken(registrado.getCorreo(), registrado.getRol().name());
            session.setAttribute("jwtToken", jwt);
            session.setAttribute("jwtUsuarioId", registrado.getId());
            session.setAttribute("jwtNombre", registrado.getNombre());
            session.setAttribute("jwtRol", registrado.getRol().name());

            res.sendRedirect(req.getContextPath() + "/app/productos?welcome=true");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("nombre", nombre);
            req.setAttribute("correo", correo);
            req.setAttribute("telefono", telefono);
            req.setAttribute("calle", calle);
            req.setAttribute("ciudad", ciudad);
            req.setAttribute("estado", estado);
            req.setAttribute("codigoPostal", codigoPostal);
            req.getRequestDispatcher("/views/auth/registro.jsp").forward(req, res);
        }
    }
}
