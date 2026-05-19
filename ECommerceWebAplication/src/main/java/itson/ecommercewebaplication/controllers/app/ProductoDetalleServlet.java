package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.bo.ProductoBO;
import itson.ecommercewebaplication.bo.ResenaBO;
import itson.ecommercewebaplication.models.Producto;
import itson.ecommercewebaplication.models.Resenia;
import itson.ecommercewebaplication.models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Controlador de la ficha de producto. El GET solo valida que el id sea
 * numérico y sirve el esqueleto de la página; los datos del producto y sus
 * reseñas los carga producto-detalle.js por Fetch a la API REST. El POST es
 * un respaldo sin JavaScript para publicar una reseña con un formulario
 * clásico, validando antes que el cliente haya comprado el producto y no
 * exceda las reseñas permitidas.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "ProductoDetalleServlet", urlPatterns = {"/app/producto"})
public class ProductoDetalleServlet extends HttpServlet {

    private ProductoBO productoBO;
    private ResenaBO resenaBO;
    private PedidoBO pedidoBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        resenaBO = new ResenaBO();
        pedidoBO = new PedidoBO();
    }

    /**
     * GET sirve el esqueleto HTML; el contenido (producto, reseñas, etc.)
     * lo carga producto-detalle.js vía Fetch a la API REST.
     * Solo valida que el id sea numérico — si el producto no existe,
     * el cliente recibe 404 de /api/productos/{id} y muestra el error.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            res.sendRedirect(req.getContextPath() + "/app/productos");
            return;
        }
        try {
            Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/app/productos");
            return;
        }

        req.setAttribute("productoId", idParam.trim());
        req.getRequestDispatcher("/views/aplication/producto-detalle.jsp").forward(req, res);
    }

    /**
     * doPost: fallback no-JS para publicar reseña vía form clásico.
     * El flujo principal va por POST /api/resenas (producto-detalle.js).
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        Usuario usuario = session != null ? (Usuario) session.getAttribute("clienteLogueado") : null;
        String idParam = req.getParameter("productoId");

        if (usuario == null || idParam == null) {
            res.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        try {
            int productoId = Integer.parseInt(idParam.trim());
            Producto producto = productoBO.obtenerPorId(productoId);
            if (producto == null) {
                res.sendRedirect(req.getContextPath() + "/app/productos");
                return;
            }

            long compras = pedidoBO.contarComprasDeProducto(usuario.getId(), productoId);
            long resenasDadas = resenaBO.contarResenasPorUsuarioYProducto(usuario.getId(), productoId);
            if (compras == 0 || resenasDadas >= compras) {
                res.sendRedirect(req.getContextPath() + "/app/producto?id=" + productoId
                        + "&resenaError=1");
                return;
            }

            int calificacion = Integer.parseInt(req.getParameter("calificacion"));
            String comentario = req.getParameter("comentario");
            Resenia resena = new Resenia(calificacion, comentario, LocalDate.now(), producto, usuario);
            resenaBO.crear(resena);

            res.sendRedirect(req.getContextPath() + "/app/producto?id=" + productoId + "&resenaOk=1");

        } catch (Exception e) {
            res.sendRedirect(req.getContextPath() + "/app/productos");
        }
    }
}
