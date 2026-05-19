package itson.ecommercewebaplication.controllers.app;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.bo.ProductoBO;
import itson.ecommercewebaplication.enums.Tallas;
import itson.ecommercewebaplication.models.DetallePedido;
import itson.ecommercewebaplication.models.Producto;
import itson.ecommercewebaplication.models.StockTalla;
import itson.ecommercewebaplication.models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del carrito en su versión sin JavaScript (respaldo del flujo
 * que normalmente maneja la API REST). Maneja dos escenarios: si el cliente
 * está logueado, opera sobre el carrito persistente vía {@code CarritoBO};
 * si es un invitado, trabaja sobre una lista guardada en la sesión. En ambos
 * casos valida el stock disponible (por talla cuando aplica) antes de agregar
 * o actualizar cantidades.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "CarritoServlet", urlPatterns = {"/app/carrito"})
public class CarritoServlet extends HttpServlet {

    private ProductoBO productoBO;
    private CarritoBO carritoBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        carritoBO = new CarritoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();

        Usuario usuario = (Usuario) session.getAttribute("clienteLogueado");
        List<DetallePedido> carrito;
        if (usuario != null) {
            carrito = carritoBO.recuperarItemsDesdeDB(usuario.getId());
            session.setAttribute("carrito", carrito);
        } else {
            @SuppressWarnings("unchecked")
            List<DetallePedido> sessCarrito = (List<DetallePedido>) session.getAttribute("carrito");
            carrito = sessCarrito;
            if (carrito == null) {
                carrito = new ArrayList<>();
                session.setAttribute("carrito", carrito);
            }
        }

        double subtotal = carrito.stream().mapToDouble(d -> d.getPrecioUnidad() * d.getCantidad()).sum();
        double costoEnvio = carrito.isEmpty() ? 0 : (subtotal >= 1500 ? 0 : 150.0);
        req.setAttribute("carrito", carrito);
        req.setAttribute("subtotal", subtotal);
        req.setAttribute("costoEnvio", costoEnvio);
        req.setAttribute("total", subtotal + costoEnvio);
        req.setAttribute("cantidadItems", carrito.stream().mapToInt(DetallePedido::getCantidad).sum());
        req.getRequestDispatcher("/views/aplication/carrito.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        HttpSession session = req.getSession();
        try {
            if ("agregar".equals(accion)) {
                agregar(req, session);
            } else if ("actualizar".equals(accion)) {
                actualizar(req, session);
            } else if ("eliminar".equals(accion)) {
                eliminar(req, session);
            } else if ("vaciar".equals(accion)) {
                Usuario usuario = (Usuario) session.getAttribute("clienteLogueado");
                if (usuario != null) {
                    try {
                        carritoBO.vaciar(usuario);
                    } catch (Exception ignored) {
                    }
                }
                session.removeAttribute("carrito");
            }
            res.sendRedirect(req.getContextPath() + "/app/carrito");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, res);
        }
    }

    private void agregar(HttpServletRequest req, HttpSession session) throws Exception {
        int productoId = Integer.parseInt(req.getParameter("productoId"));
        int cantidad = Integer.parseInt(req.getParameter("cantidad"));
        String tallaStr = req.getParameter("talla");

        if (cantidad <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero.");
        }

        Usuario usuario = (Usuario) session.getAttribute("clienteLogueado");
        if (usuario != null) {
            carritoBO.agregarItem(usuario, productoId, cantidad, tallaStr);
            return;
        }

        Producto producto = productoBO.obtenerPorId(productoId);
        if (producto == null) {
            throw new Exception("El producto seleccionado no existe.");
        }
        int stockDisponible = obtenerStockDisponible(producto, tallaStr);
        if (stockDisponible < cantidad) {
            throw new Exception("Stock insuficiente para la talla "
                    + (tallaStr != null ? tallaStr : "")
                    + ". Solo quedan " + stockDisponible + " unidades.");
        }

        @SuppressWarnings("unchecked")
        List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        boolean encontrado = false;
        for (DetallePedido d : carrito) {
            boolean mismoProducto = d.getProducto().getId() == productoId;
            boolean mismaTalla = tallaStr == null
                    ? d.getTalla() == null
                    : tallaStr.equals(d.getTalla());
            if (mismoProducto && mismaTalla) {
                int nueva = d.getCantidad() + cantidad;
                if (stockDisponible < nueva) {
                    throw new Exception("No hay suficiente stock de la talla "
                            + (tallaStr != null ? tallaStr : "")
                            + ". Disponible: " + stockDisponible);
                }
                d.setCantidad(nueva);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            DetallePedido item = new DetallePedido(cantidad, producto.getPrecio(), producto);
            item.setTalla(tallaStr);
            carrito.add(item);
        }
        session.setAttribute("carrito", carrito);
    }

    private void actualizar(HttpServletRequest req, HttpSession session) throws Exception {
        int productoId = Integer.parseInt(req.getParameter("productoId"));
        int cantidad = Integer.parseInt(req.getParameter("cantidad"));
        String tallaStr = req.getParameter("talla");
        if (cantidad <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero.");
        }

        Usuario usuario = (Usuario) session.getAttribute("clienteLogueado");
        if (usuario != null) {
            carritoBO.actualizarCantidad(usuario, productoId, cantidad, tallaStr);
            return;
        }

        @SuppressWarnings("unchecked")
        List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
        if (carrito != null) {
            for (DetallePedido d : carrito) {
                boolean mismoProducto = d.getProducto().getId() == productoId;
                boolean mismaTalla = tallaStr == null
                        ? d.getTalla() == null
                        : tallaStr.equals(d.getTalla());
                if (mismoProducto && mismaTalla) {
                    Producto p = productoBO.obtenerPorId(productoId);
                    int stock = obtenerStockDisponible(p, tallaStr);
                    if (stock < cantidad) {
                        throw new Exception("No hay suficiente stock disponible.");
                    }
                    d.setCantidad(cantidad);
                    break;
                }
            }
            session.setAttribute("carrito", carrito);
        }
    }

    private void eliminar(HttpServletRequest req, HttpSession session) throws Exception {
        int productoId = Integer.parseInt(req.getParameter("productoId"));
        String tallaStr = req.getParameter("talla");

        Usuario usuario = (Usuario) session.getAttribute("clienteLogueado");
        if (usuario != null) {
            carritoBO.eliminarItem(usuario, productoId, tallaStr);
            return;
        }

        @SuppressWarnings("unchecked")
        List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
        if (carrito != null) {
            carrito.removeIf(d -> {
                boolean mismoProducto = d.getProducto().getId() == productoId;
                boolean mismaTalla = tallaStr == null
                        ? d.getTalla() == null
                        : tallaStr.equals(d.getTalla());
                return mismoProducto && mismaTalla;
            });
            session.setAttribute("carrito", carrito);
        }
    }

    /**
     * Devuelve el stock disponible de un producto para la talla indicada.
     * Si el producto maneja tallas, busca la cantidad de esa talla; si no,
     * o si la talla es inválida, regresa el stock general del producto.
     */
    private int obtenerStockDisponible(Producto producto, String tallaStr) {
        List<StockTalla> stockPorTalla = producto.getStockPorTalla();
        if (stockPorTalla != null && !stockPorTalla.isEmpty() && tallaStr != null) {
            try {
                Tallas talla = Tallas.valueOf(tallaStr);
                for (StockTalla st : stockPorTalla) {
                    if (st.getTalla() == talla) {
                        return st.getCantidad();
                    }
                }
                return 0; 
            } catch (IllegalArgumentException e) {
                // talla inválida 
            }
        }
        return producto.getStock();
    }
}
