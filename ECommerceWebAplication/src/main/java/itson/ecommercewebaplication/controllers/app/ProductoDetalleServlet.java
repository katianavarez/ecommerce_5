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
 *
 * @author PC
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
            int id = Integer.parseInt(idParam.trim());
            Producto producto = productoBO.obtenerPorId(id);

            if (producto == null) {
                res.sendRedirect(req.getContextPath() + "/app/productos");
                return;
            }

            req.setAttribute("producto", producto);
            req.setAttribute("promedio", resenaBO.calcularPromedio(id));
            req.setAttribute("totalResenas", resenaBO.contarPorProducto(id));

            // Pasar la lista de StockTalla directamente — el JSP la procesa con c:forEach
            req.setAttribute("stockPorTallaList",
                    producto.getStockPorTalla() != null
                    ? producto.getStockPorTalla()
                    : java.util.Collections.emptyList());

            // Cargar resenias según si hay sesión o no
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("clienteLogueado") != null) {
                Usuario usuario = (Usuario) session.getAttribute("clienteLogueado");
                req.setAttribute("usuarioLogueado", usuario);

                long compras = pedidoBO.contarComprasDeProducto(usuario.getId(), id);
                long resenasDadas = resenaBO.contarResenasPorUsuarioYProducto(usuario.getId(), id);
                long resenasRestantes = compras - resenasDadas;

                req.setAttribute("puedeResenar", resenasRestantes > 0);
                req.setAttribute("resenasRestantes", resenasRestantes);
                req.setAttribute("yaCompro", compras > 0);

                // Resenias propias del usuario (todas) + últimas 5 de otros
                req.setAttribute("resenasUsuario", resenaBO.obtenerDelUsuarioPorProducto(usuario.getId(), id));
                req.setAttribute("resenasOtros", resenaBO.obtenerRecientesPorProducto(id, usuario.getId(), 5));
            } else {
                // Sin sesión: mostrar solo las últimas 5
                req.setAttribute("resenasUsuario", java.util.Collections.emptyList());
                req.setAttribute("resenasOtros", resenaBO.obtenerRecientesPorProducto(id, 0, 5));
            }

            req.getRequestDispatcher("/views/aplication/producto-detalle.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/app/productos");
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo cargar el producto. Por favor intenta más tarde.");
            req.getRequestDispatcher("/views/aplication/producto-detalle.jsp").forward(req, res);
        }
    }

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

        int productoId = -1;
        try {
            productoId = Integer.parseInt(idParam.trim());
            Producto producto = productoBO.obtenerPorId(productoId);

            if (producto == null) {
                res.sendRedirect(req.getContextPath() + "/app/productos");
                return;
            }

            // Verificar cuántas resenias puede dejar aún
            long compras = pedidoBO.contarComprasDeProducto(usuario.getId(), productoId);
            long resenasDadas = resenaBO.contarResenasPorUsuarioYProducto(usuario.getId(), productoId);
            long resenasRestantes = compras - resenasDadas;

            if (compras == 0) {
                req.setAttribute("errorResena", "Solo puedes reseñar productos que hayas comprado.");
                cargarDetalle(req, res, productoId, usuario);
                return;
            }

            if (resenasRestantes <= 0) {
                req.setAttribute("errorResena",
                        "Ya has dejado el máximo de resenias permitidas para este producto (" + compras + ").");
                cargarDetalle(req, res, productoId, usuario);
                return;
            }

            int calificacion = Integer.parseInt(req.getParameter("calificacion"));
            String comentario = req.getParameter("comentario");

            Resenia resena = new Resenia(calificacion, comentario, LocalDate.now(), producto, usuario);
            resenaBO.crear(resena);

            res.sendRedirect(req.getContextPath() + "/app/producto?id=" + productoId + "&resenaOk=1");

        } catch (Exception e) {
            req.setAttribute("errorResena", "No se pudo publicar la reseña: " + e.getMessage());
            if (productoId > 0) {
                cargarDetalle(req, res, productoId, usuario);
            } else {
                res.sendRedirect(req.getContextPath() + "/app/productos");
            }
        }
    }

    private void cargarDetalle(HttpServletRequest req, HttpServletResponse res,
            int productoId, Usuario usuario)
            throws ServletException, IOException {
        Producto producto = productoBO.obtenerPorId(productoId);
        req.setAttribute("producto", producto);
        req.setAttribute("promedio", resenaBO.calcularPromedio(productoId));
        req.setAttribute("totalResenas", resenaBO.contarPorProducto(productoId));
        req.setAttribute("usuarioLogueado", usuario);

        // Pasar la lista de StockTalla directamente — el JSP la procesa con c:forEach
        req.setAttribute("stockPorTallaList",
                (producto != null && producto.getStockPorTalla() != null)
                ? producto.getStockPorTalla()
                : java.util.Collections.emptyList());

        long compras = pedidoBO.contarComprasDeProducto(usuario.getId(), productoId);
        long resenasDadas = resenaBO.contarResenasPorUsuarioYProducto(usuario.getId(), productoId);
        long resenasRestantes = compras - resenasDadas;

        req.setAttribute("puedeResenar", resenasRestantes > 0);
        req.setAttribute("resenasRestantes", resenasRestantes);
        req.setAttribute("yaCompro", compras > 0);
        req.setAttribute("resenasUsuario", resenaBO.obtenerDelUsuarioPorProducto(usuario.getId(), productoId));
        req.setAttribute("resenasOtros", resenaBO.obtenerRecientesPorProducto(productoId, usuario.getId(), 5));

        req.getRequestDispatcher("/views/aplication/producto-detalle.jsp").forward(req, res);
    }
}
