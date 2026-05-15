package itson.ecommercewebaplication.controllers.admin;

import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.enums.EstadoPago;
import itson.ecommercewebaplication.enums.FormaPago;
import itson.ecommercewebaplication.models.Pedido;
import itson.ecommercewebaplication.util.PaginacionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "PagoAdminServlet", urlPatterns = {"/admin/pagos"})
public class PagoAdminServlet extends HttpServlet {

    private PedidoBO pedidoBO;

    @Override
    public void init() throws ServletException {
        pedidoBO = new PedidoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            List<Pedido> pedidosConPago = pedidoBO.obtenerTodosConPago();

            Map<String, Long> conteosPorEstado = new LinkedHashMap<>();
            conteosPorEstado.put("TODOS", (long) pedidosConPago.size());
            for (EstadoPago e : EstadoPago.values()) {
                long count = pedidosConPago.stream()
                        .filter(p -> p.getPago() != null && p.getPago().getEstado() == e)
                        .count();
                conteosPorEstado.put(e.name(), count);
            }

            String estadoFiltro = req.getParameter("estadoPago");
            String metodoFiltro = req.getParameter("metodoPago");
            String busqueda = req.getParameter("busqueda");

            List<Pedido> filtrados = pedidosConPago;
            if (estadoFiltro != null && !estadoFiltro.isBlank() && !"TODOS".equals(estadoFiltro)) {
                try {
                    EstadoPago e = EstadoPago.valueOf(estadoFiltro);
                    filtrados = filtrados.stream()
                            .filter(p -> p.getPago().getEstado() == e)
                            .collect(Collectors.toList());
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (metodoFiltro != null && !metodoFiltro.isBlank() && !"TODOS".equals(metodoFiltro)) {
                try {
                    FormaPago m = FormaPago.valueOf(metodoFiltro);
                    filtrados = filtrados.stream()
                            .filter(p -> p.getPago().getMetodo() == m)
                            .collect(Collectors.toList());
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (busqueda != null && !busqueda.isBlank()) {
                String q = busqueda.toLowerCase().trim();
                filtrados = filtrados.stream()
                        .filter(p -> {
                            boolean matchPedido = p.getNumPedido() != null
                                    && p.getNumPedido().toLowerCase().contains(q);
                            boolean matchCliente = p.getUsuario() != null
                                    && (p.getUsuario().getNombre() != null
                                    && p.getUsuario().getNombre().toLowerCase().contains(q)
                                    || p.getUsuario().getCorreo() != null
                                    && p.getUsuario().getCorreo().toLowerCase().contains(q));
                            return matchPedido || matchCliente;
                        })
                        .collect(Collectors.toList());
            }

            PaginacionUtil.paginar(req, filtrados, "pedidosPagos", PaginacionUtil.TAMANO_ADMIN);
            req.setAttribute("estadoSeleccionado", estadoFiltro != null ? estadoFiltro : "TODOS");
            req.setAttribute("metodoSeleccionado", metodoFiltro != null ? metodoFiltro : "TODOS");
            req.setAttribute("busquedaActual", busqueda != null ? busqueda : "");
            req.setAttribute("estadosPago", EstadoPago.values());
            req.setAttribute("metodosPago", FormaPago.values());
            req.setAttribute("conteosPorEstado", conteosPorEstado);
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo cargar el historial de pagos.");
            req.setAttribute("pedidosPagos", Collections.emptyList());
            req.setAttribute("estadosPago", EstadoPago.values());
            req.setAttribute("metodosPago", FormaPago.values());
        }
        req.getRequestDispatcher("/views/admin/admin-pagos.jsp").forward(req, res);
    }
}
