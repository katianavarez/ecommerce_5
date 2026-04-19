package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.CategoriaBO;
import itson.ecommercewebaplication.bo.ProductoBO;
import itson.ecommercewebaplication.dto.ProductoRequestDTO;
import itson.ecommercewebaplication.dto.ResponseDTO;
import itson.ecommercewebaplication.enums.Tallas;
import itson.ecommercewebaplication.models.Categoria;
import itson.ecommercewebaplication.models.Producto;
import itson.ecommercewebaplication.util.JSONMapper;
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
@WebServlet(name = "ProductosApiServlet", urlPatterns = {"/api/productos/*"})
public class ProductosApiServlet extends HttpServlet {

    private ProductoBO productoBO;
    private CategoriaBO categoriaBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        categoriaBO = new CategoriaBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                String busqueda = req.getParameter("busqueda");
                String categoriaIdStr = req.getParameter("categoria");
                String paginaStr = req.getParameter("pagina");
                List<Producto> productos;
                if (busqueda != null && !busqueda.isBlank()) {
                    productos = productoBO.buscarPorNombre(busqueda);
                } else if (categoriaIdStr != null) {
                    productos = productoBO.obtenerPorCategoria(Integer.parseInt(categoriaIdStr));
                } else if (paginaStr != null) {
                    int pagina = Integer.parseInt(paginaStr);
                    productos = productoBO.obtenerPaginados(pagina, 9);
                } else {
                    productos = productoBO.obtenerTodos();
                }
                JSONMapper.mapper.writeValue(res.getWriter(), productos);
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Producto p = productoBO.obtenerPorId(id);
                if (p == null) {
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, "Producto no encontrado"));
                } else {
                    JSONMapper.mapper.writeValue(res.getWriter(), p);
                }
            }
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            checkAdmin(req);
            ProductoRequestDTO dto = JSONMapper.mapper.readValue(req.getInputStream(), ProductoRequestDTO.class);
            Producto p = buildFromDTO(dto);
            Producto creado = productoBO.crear(p);
            res.setStatus(HttpServletResponse.SC_CREATED);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(true, "Producto creado", creado));
        } catch (SecurityException se) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, se.getMessage()));
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
            checkAdmin(req);
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                throw new IllegalArgumentException("ID requerido");
            }
            int id = Integer.parseInt(pathInfo.substring(1));
            ProductoRequestDTO dto = JSONMapper.mapper.readValue(req.getInputStream(), ProductoRequestDTO.class);
            Producto p = buildFromDTO(dto);
            p.setId(id);
            Producto actualizado = productoBO.actualizar(p);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(true, "Producto actualizado", actualizado));
        } catch (SecurityException se) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, se.getMessage()));
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
            if (pathInfo == null || pathInfo.equals("/")) {
                throw new IllegalArgumentException("ID requerido");
            }
            int id = Integer.parseInt(pathInfo.substring(1));
            productoBO.eliminar(id);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(true, "Producto eliminado"));
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
        if (!"ADMINISTRADOR".equals(rol)) {
            throw new SecurityException("Acceso denegado. Solo administradores.");
        }
    }

    private Producto buildFromDTO(ProductoRequestDTO dto) {
        Categoria categoria = categoriaBO.obtenerPorId(dto.getCategoriaId());
        List<Tallas> tallas = new ArrayList<>();
        if (dto.getTallasDisponibles() != null) {
            for (String t : dto.getTallasDisponibles()) {
                tallas.add(Tallas.valueOf(t));
            }
        }
        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        p.setImagenURL(dto.getImagenURL());
        p.setStock(dto.getStock());
        if (dto.getColor() != null && !dto.getColor().isBlank()) {
            try {
                itson.ecommercewebaplication.enums.Colores.valueOf(dto.getColor()); 
                p.setColor(dto.getColor()); 
            } catch (IllegalArgumentException ignored) {
                p.setColor(null);
            }
        }
        p.setCategoria(categoria);
        p.setTallasDisponibles(tallas);
        return p;
    }
}
