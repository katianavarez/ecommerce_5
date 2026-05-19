package itson.ecommercewebaplication.controllers.admin;

import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.enums.EstadoPedido;
import itson.ecommercewebaplication.models.Pedido;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import itson.ecommercewebaplication.util.PaginacionUtil;
import java.util.List;
import java.util.Map;

/**
 * Controlador de la gestión de pedidos en el panel admin. Permite listar con
 * filtros (estado, búsqueda, orden), ver el detalle de un pedido y cambiar su
 * estado o cancelarlo. Antes de cambiar el estado compara el estado que vio el
 * admin contra el actual en BD; si otro admin ya lo movió, redirige con un
 * aviso de "stale" para no pisar el cambio. Manda cabeceras anti-caché para
 * que el navegador no muestre un estado desactualizado al volver atrás.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "PedidoAdminServlet", urlPatterns = {"/admin/pedidos"})
public class PedidoAdminServlet extends HttpServlet {

    private PedidoBO pedidoBO;

    @Override
    public void init() throws ServletException {
        pedidoBO = new PedidoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);
        String accion = req.getParameter("accion");
        try {
            if ("detalle".equals(accion)) {
                detalle(req, res);
            } else {
                listar(req, res);
            }
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo cargar la información de pedidos.");
            cargarConteos(req);
            req.setAttribute("pedidos", Collections.emptyList());
            req.setAttribute("estados", EstadoPedido.values());
            req.getRequestDispatcher("/views/admin/admin-pedidos.jsp").forward(req, res);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        try {
            if ("actualizarEstado".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String nuevoEstadoStr = req.getParameter("nuevoEstado");
                EstadoPedido nuevoEstado;
                try {
                    nuevoEstado = EstadoPedido.valueOf(nuevoEstadoStr);
                } catch (IllegalArgumentException | NullPointerException ex) {
                    throw new Exception("Estado de pedido inválido: " + nuevoEstadoStr);
                }
                String estadoActualStr = req.getParameter("estadoActual");

                Pedido pedidoActual = pedidoBO.obtenerPorId(id);
                if (pedidoActual == null) {
                    throw new Exception("Pedido no encontrado.");
                }

                if (estadoActualStr != null && !pedidoActual.getEstado().name().equals(estadoActualStr)) {
                    res.sendRedirect(req.getContextPath()
                            + "/admin/pedidos?accion=detalle&id=" + id + "&warn=stale");
                    return;
                }

                pedidoBO.actualizarEstado(id, nuevoEstado);
                res.sendRedirect(req.getContextPath() + "/admin/pedidos?accion=detalle&id=" + id + "&success=updated");
            } else if ("cancelar".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                pedidoBO.cancelarPedido(id);
                res.sendRedirect(req.getContextPath() + "/admin/pedidos?accion=detalle&id=" + id + "&success=cancelled");
            }
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo actualizar el pedido. " + e.getMessage());
            listar(req, res);
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String estado = req.getParameter("estado");   
        String busqueda = req.getParameter("busqueda");
        String orden = req.getParameter("orden");     

        if (orden == null || orden.isBlank()) {
            orden = "reciente";
        }
        if (estado == null || estado.isBlank()) {
            estado = "TODOS";
        }

        List<Pedido> pedidos = pedidoBO.buscarYFiltrar(estado, busqueda, orden);

        PaginacionUtil.paginar(req, pedidos, "pedidos", PaginacionUtil.TAMANO_ADMIN);
        req.setAttribute("estados", EstadoPedido.values());
        req.setAttribute("estadoSeleccionado", estado);
        req.setAttribute("busquedaActual", busqueda != null ? busqueda : "");
        req.setAttribute("ordenActual", orden);
        cargarConteos(req);
        req.getRequestDispatcher("/views/admin/admin-pedidos.jsp").forward(req, res);
    }

    private void detalle(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Pedido pedido = pedidoBO.obtenerPorId(id);
        if (pedido == null) {
            req.setAttribute("error", "El pedido solicitado no fue encontrado.");
            listar(req, res);
            return;
        }
        req.setAttribute("pedido", pedido);
        req.setAttribute("estados", EstadoPedido.values());
        cargarConteos(req);
        req.getRequestDispatcher("/views/admin/admin-pedidos.jsp").forward(req, res);
    }

    private void cargarConteos(HttpServletRequest req) {
        try {
            long total = pedidoBO.obtenerTodos().size();
            Map<String, Long> conteos = new LinkedHashMap<>();
            conteos.put("TODOS", total);
            for (EstadoPedido e : EstadoPedido.values()) {
                conteos.put(e.name(), pedidoBO.contarPorEstado(e.name()));
            }
            req.setAttribute("conteosPorEstado", conteos);
        } catch (Exception ignored) {
        }
    }
}
