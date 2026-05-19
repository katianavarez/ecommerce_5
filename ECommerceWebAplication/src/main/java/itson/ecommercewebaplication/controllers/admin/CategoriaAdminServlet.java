package itson.ecommercewebaplication.controllers.admin;

import itson.ecommercewebaplication.bo.CategoriaBO;
import itson.ecommercewebaplication.models.Categoria;
import itson.ecommercewebaplication.util.PaginacionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Controlador del CRUD de categorías en el panel admin. El GET resuelve si
 * se muestra el formulario de alta/edición o el listado paginado; el POST
 * atiende crear, actualizar y eliminar. Las validaciones de negocio (nombre
 * único, no borrar categorías con productos) viven en {@code CategoriaBO};
 * aquí solo se enrutan las peticiones y se redirige con mensajes de éxito o
 * error.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "CategoriaAdminServlet", urlPatterns = {"/admin/categorias"})
public class CategoriaAdminServlet extends HttpServlet {

    private CategoriaBO categoriaBO;

    @Override
    public void init() throws ServletException {
        categoriaBO = new CategoriaBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        try {
            if ("nuevo".equals(accion)) {
                req.getRequestDispatcher("/views/admin/admin-categoria-form.jsp").forward(req, res);
            } else if ("editar".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Categoria cat = categoriaBO.obtenerPorId(id);
                if (cat == null) {
                    res.sendRedirect(req.getContextPath() + "/admin/categorias?error="
                            + java.net.URLEncoder.encode("Categoría no encontrada.", "UTF-8"));
                    return;
                }
                req.setAttribute("categoria", cat);
                req.getRequestDispatcher("/views/admin/admin-categoria-form.jsp").forward(req, res);
            } else {
                listar(req, res);
            }
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo completar la operación. " + e.getMessage());
            listar(req, res);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        try {
            if ("crear".equals(accion)) {
                Categoria nueva = new Categoria();
                nueva.setNombre(req.getParameter("nombre"));
                categoriaBO.crear(nueva);
                res.sendRedirect(req.getContextPath() + "/admin/categorias?success=created");
            } else if ("actualizar".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Categoria cat = categoriaBO.obtenerPorId(id);
                if (cat == null) {
                    throw new Exception("Categoría no encontrada.");
                }
                cat.setNombre(req.getParameter("nombre"));
                categoriaBO.actualizar(cat);
                res.sendRedirect(req.getContextPath() + "/admin/categorias?success=updated");
            } else if ("eliminar".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                categoriaBO.eliminar(id);
                res.sendRedirect(req.getContextPath() + "/admin/categorias?success=deleted");
            }
        } catch (Exception e) {
            res.sendRedirect(req.getContextPath() + "/admin/categorias?error="
                    + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        List<Categoria> categorias = categoriaBO.obtenerTodas();
        req.setAttribute("conteoProductos", categoriaBO.contarProductosPorCategoria());
        PaginacionUtil.paginar(req, categorias, "categorias", PaginacionUtil.TAMANO_ADMIN);
        if (req.getParameter("error") != null) {
            req.setAttribute("error", req.getParameter("error"));
        }
        req.getRequestDispatcher("/views/admin/admin-categorias.jsp").forward(req, res);
    }
}
