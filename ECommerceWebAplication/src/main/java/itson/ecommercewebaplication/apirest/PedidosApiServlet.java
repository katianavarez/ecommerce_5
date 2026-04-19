package itson.ecommercewebaplication.apirest;

import itson.ecommercewebaplication.bo.CarritoBO;
import itson.ecommercewebaplication.bo.PedidoBO;
import itson.ecommercewebaplication.bo.UsuarioBO;
import itson.ecommercewebaplication.dto.PedidoRequestDTO;
import itson.ecommercewebaplication.dto.ResponseDTO;
import itson.ecommercewebaplication.enums.FormaPago;
import itson.ecommercewebaplication.models.*;
import itson.ecommercewebaplication.util.JSONMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author PC
 */
@WebServlet(name = "PedidosApiServlet", urlPatterns = {"/api/pedidos/*"})
public class PedidosApiServlet extends HttpServlet {

    private PedidoBO pedidoBO;
    private UsuarioBO usuarioBO;
    private CarritoBO carritoBO;

    @Override
    public void init() throws ServletException {
        pedidoBO = new PedidoBO();
        usuarioBO = new UsuarioBO();
        carritoBO = new CarritoBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            String pathInfo = req.getPathInfo(); // /{id}  o  /usuario/{id}
            if (pathInfo == null || pathInfo.equals("/")) {
                // Listar todos (solo admin)
                checkAdmin(req);
                JSONMapper.mapper.writeValue(res.getWriter(), pedidoBO.obtenerTodos());
                return;
            }
            if (pathInfo.startsWith("/usuario/")) {
                int usuarioId = Integer.parseInt(pathInfo.substring("/usuario/".length()));
                List<Pedido> pedidos = pedidoBO.obtenerPorUsuario(usuarioId);
                JSONMapper.mapper.writeValue(res.getWriter(), pedidos);
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Pedido pedido = pedidoBO.obtenerPorId(id);
                if (pedido == null) {
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, "Pedido no encontrado"));
                } else {
                    JSONMapper.mapper.writeValue(res.getWriter(), pedido);
                }
            }
        } catch (SecurityException se) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(false, se.getMessage()));
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
            String correo = (String) req.getAttribute("correoUsuario");
            Usuario usuario = usuarioBO.obtenerPorCorreo(correo);
            PedidoRequestDTO dto = JSONMapper.mapper.readValue(req.getInputStream(), PedidoRequestDTO.class);

            Carrito carrito = carritoBO.obtenerOCrear(usuario);
            if (carrito.getDetalles().isEmpty()) {
                throw new Exception("El carrito está vacío");
            }

            Direccion direccion = new Direccion(dto.getCalle(), dto.getCiudad(), dto.getEstado(), dto.getCodigoPostal());
            FormaPago metodoPago = FormaPago.valueOf(dto.getMetodoPago());
            Pedido pedido = pedidoBO.crearPedido(usuario, direccion, carrito.getDetalles(), metodoPago);
            carritoBO.vaciar(usuario);

            res.setStatus(HttpServletResponse.SC_CREATED);
            JSONMapper.mapper.writeValue(res.getWriter(), new ResponseDTO(true, "Pedido creado exitosamente", pedido));
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
}
