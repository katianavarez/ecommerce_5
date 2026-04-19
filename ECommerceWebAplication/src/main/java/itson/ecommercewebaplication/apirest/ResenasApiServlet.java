package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.ProductoBO;
import itson.ecommercewebaplication.bo.ResenaBO;
import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.dto.ResenaRequestDTO;
import itson.ecommercewebaplication.dto.ResponseDTO;
import itson.ecommercewebaplication.models.Resenia;
import itson.ecommercewebaplication.util.JSONMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

/**
 *
 * @author PC
 */
@WebServlet(name = "ResenasApiServlet", urlPatterns = {"/api/resenas/*"})
public class ResenasApiServlet extends HttpServlet {

    private ResenaBO resenaBO;
    private UsuarioBO usuarioBO;
    private ProductoBO productoBO;

    @Override
    public void init() throws ServletException {
        resenaBO = new ResenaBO();
        usuarioBO = new UsuarioBO();
        productoBO = new ProductoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.startsWith("/producto/")) {
                int productoId = Integer.parseInt(pathInfo.substring("/producto/".length()));
                JSONMapper.mapper.writeValue(res.getWriter(), resenaBO.obtenerPorProducto(productoId));
            } else {
                checkAdmin(req);
                JSONMapper.mapper.writeValue(res.getWriter(), resenaBO.obtenerTodas());
            }
        } catch (SecurityException se) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, se.getMessage()));
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
            ResenaRequestDTO dto = JSONMapper.mapper.readValue(req.getInputStream(), ResenaRequestDTO.class);
            String correo = (String) req.getAttribute("correoUsuario");
            Resenia resena = new Resenia(
                dto.getCalificacion(), dto.getComentario(), LocalDate.now(),
                productoBO.obtenerPorId(dto.getProductoId()),
                usuarioBO.obtenerPorCorreo(correo)
            );
            Resenia creada = resenaBO.crear(resena);
            res.setStatus(HttpServletResponse.SC_CREATED);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(true, "Resenia creada", creada));
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
            checkAdmin(req);
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) throw new IllegalArgumentException("ID requerido");
            int id = Integer.parseInt(pathInfo.substring(1));
            resenaBO.eliminar(id);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(true, "Resenia eliminada"));
        } catch (SecurityException se) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, se.getMessage()));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, e.getMessage()));
        }
    }

    private void checkAdmin(HttpServletRequest req) {
        String rol = (String) req.getAttribute("rolUsuario");
        if (!"ADMINISTRADOR".equals(rol)) throw new SecurityException("Acceso denegado. Solo administradores.");
    }
}
