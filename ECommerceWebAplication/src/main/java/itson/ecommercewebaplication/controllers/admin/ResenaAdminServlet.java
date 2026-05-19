package itson.ecommercewebaplication.controllers.admin;

import itson.ecommercewebaplication.bo.ResenaBO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import itson.ecommercewebaplication.util.PaginacionUtil;

/**
 * Controlador de la moderación de reseñas en el panel admin. Lista todas las
 * reseñas (o las de un producto concreto) de forma paginada y permite
 * eliminar las inapropiadas, cumpliendo el requisito de moderación del
 * Avance 3. La eliminación se hace por POST para evitar borrados accidentales
 * vía GET.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
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
            // Reusar PaginacionUtil para que la JSP reciba los atributos esperados
            // (paginaActual, totalPaginas, totalRegistros) y no se rompa la paginación.
            PaginacionUtil.paginar(req, resenaBO.obtenerTodas(), "resenas", PaginacionUtil.TAMANO_ADMIN);
            req.getRequestDispatcher("/views/admin/admin-resenas.jsp").forward(req, res);
        }
    }
}
