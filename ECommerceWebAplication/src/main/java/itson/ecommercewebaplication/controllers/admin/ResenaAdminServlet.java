package itson.ecommercewebaplication.controllers.admin;

import itson.ecommercewebaplication.bo.ResenaBO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import itson.ecommercewebaplication.util.PaginacionUtil;

/**
 *
 * @author PC
 */
@WebServlet(name = "ResenaAdminServlet", urlPatterns = {"/admin/resenas"})
public class ResenaAdminServlet extends HttpServlet {

    private ResenaBO resenaBO;

    @Override
    public void init() throws ServletException {
        resenaBO = new ResenaBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String accion = req.getParameter("accion");
            if (accion == null || accion.equals("listar")) {
                PaginacionUtil.paginar(req, resenaBO.obtenerTodas(), "resenas", PaginacionUtil.TAMANO_ADMIN);
            } else if (accion.equals("porProducto")) {
                int productoId = Integer.parseInt(req.getParameter("productoId"));
                PaginacionUtil.paginar(req, resenaBO.obtenerPorProducto(productoId), "resenas", PaginacionUtil.TAMANO_ADMIN);
                req.setAttribute("productoId", productoId);
            }
        } catch (Exception e) {
            req.setAttribute("error", "No se pudieron cargar las resenias.");
            req.setAttribute("resenas", java.util.Collections.emptyList());
        }
        req.getRequestDispatcher("/views/admin/admin-resenas.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            if ("eliminar".equals(req.getParameter("accion"))) {
                int id = Integer.parseInt(req.getParameter("id"));
                resenaBO.eliminar(id);
                res.sendRedirect(req.getContextPath() + "/admin/resenas?success=deleted");
            }
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo eliminar la reseña. Intenta nuevamente.");
            req.setAttribute("resenas", resenaBO.obtenerTodas());
            req.getRequestDispatcher("/views/admin/admin-resenas.jsp").forward(req, res);
        }
    }
}
