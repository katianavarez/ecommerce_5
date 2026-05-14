package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.models.Direccion;
import itson.ecommercewebaplication.models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 *
 * @author PC
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
            usuario.setContraseña(contrasena);
            usuario.setTelefono(telefono);
            Direccion direccion = new Direccion(calle, ciudad, estado, codigoPostal);
            usuarioBO.registrar(usuario, direccion);
            res.sendRedirect(req.getContextPath() + "/auth/login?registered=true");
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
