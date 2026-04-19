package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.dto.CarritoItemRequestDTO;
import itson.ecommercewebaplication.dto.ResponseDTO;
import itson.ecommercewebaplication.models.Carrito;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JSONMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 *
 * @author PC
 */
@WebServlet(name = "CarritoApiServlet", urlPatterns = {"/api/carrito/*"})
public class CarritoApiServlet extends HttpServlet {

    private CarritoBO carritoBO;
    private UsuarioBO usuarioBO;

    @Override
    public void init() throws ServletException {
        carritoBO = new CarritoBO();
        usuarioBO = new UsuarioBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                throw new IllegalArgumentException("usuarioId requerido");
            }
            int usuarioId = Integer.parseInt(pathInfo.substring(1));
            Usuario usuario = usuarioBO.obtenerPorId(usuarioId);
            if (usuario == null) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, "Usuario no encontrado"));
                return;
            }
            Carrito carrito = carritoBO.obtenerOCrear(usuario);
            JSONMapper.mapper.writeValue(res.getWriter(), carrito);
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String correo = (String) req.getAttribute("correoUsuario");
            Usuario usuario = usuarioBO.obtenerPorCorreo(correo);
            CarritoItemRequestDTO dto = JSONMapper.mapper.readValue(req.getInputStream(), CarritoItemRequestDTO.class);
            Carrito carrito = carritoBO.agregarItem(usuario, dto.getProductoId(), dto.getCantidad());
            res.setStatus(HttpServletResponse.SC_CREATED);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(true, "Producto agregado al carrito", carrito));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                throw new IllegalArgumentException("productoId requerido");
            }
            int productoId = Integer.parseInt(pathInfo.substring(1));
            String correo = (String) req.getAttribute("correoUsuario");
            Usuario usuario = usuarioBO.obtenerPorCorreo(correo);
            CarritoItemRequestDTO dto = JSONMapper.mapper.readValue(req.getInputStream(), CarritoItemRequestDTO.class);
            Carrito carrito = carritoBO.actualizarCantidad(usuario, productoId, dto.getCantidad());
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(true, "Cantidad actualizada", carrito));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                throw new IllegalArgumentException("productoId requerido");
            }
            int productoId = Integer.parseInt(pathInfo.substring(1));
            String correo = (String) req.getAttribute("correoUsuario");
            Usuario usuario = usuarioBO.obtenerPorCorreo(correo);
            carritoBO.eliminarItem(usuario, productoId);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(true, "Producto eliminado del carrito"));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, e.getMessage()));
        }
    }
}
