package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.PedidoDAO;
import itson.ecommercewebaplication.enums.EstadoPago;
import itson.ecommercewebaplication.enums.EstadoPedido;
import itson.ecommercewebaplication.enums.FormaPago;
import itson.ecommercewebaplication.models.*;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author PC
 */
public class PedidoBO {

    public static final double ENVIO_GRATIS_DESDE = 1500.0;
    public static final double COSTO_ENVIO = 150.0;

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    public static double calcularEnvio(double subtotal) {
        return subtotal >= ENVIO_GRATIS_DESDE ? 0.0 : COSTO_ENVIO;
    }

    public List<Pedido> obtenerTodos() {
        return pedidoDAO.obtenerTodos();
    }

    public Pedido obtenerPorId(int id) {
        return pedidoDAO.obtenerPorId(id);
    }

    public List<Pedido> obtenerPorUsuario(int usuarioId) {
        return pedidoDAO.obtenerPorUsuario(usuarioId);
    }

    public List<Pedido> obtenerPorEstado(EstadoPedido estado) {
        return pedidoDAO.obtenerPorEstado(estado.name());
    }

    public Pedido crearPedido(Usuario usuario, Direccion direccion,
            List<DetallePedido> detalles, FormaPago metodoPago) throws Exception {
        if (usuario == null) {
            throw new Exception("El usuario es requerido");
        }
        if (direccion == null) {
            throw new Exception("La dirección de envío es requerida");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new Exception("El pedido debe tener al menos un producto");
        }
        if (metodoPago == null) {
            throw new Exception("El método de pago es requerido");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            double total = 0;
            for (DetallePedido detalle : detalles) {
                Producto producto = em.find(Producto.class, detalle.getProducto().getId());
                if (producto == null) {
                    throw new Exception("Producto no encontrado: " + detalle.getProducto().getId());
                }

                String tallaStr = detalle.getTalla();
                boolean productoTieneTallas = producto.getStockPorTalla() != null
                        && !producto.getStockPorTalla().isEmpty();

                if (productoTieneTallas) {
                    if (tallaStr == null || tallaStr.isBlank()) {
                        throw new Exception("Debes seleccionar una talla para: "
                                + producto.getNombre());
                    }
                    try {
                        itson.ecommercewebaplication.enums.Tallas tallaEnum
                                = itson.ecommercewebaplication.enums.Tallas.valueOf(tallaStr);
                        boolean tallaEncontrada = false;
                        for (itson.ecommercewebaplication.models.StockTalla st
                                : producto.getStockPorTalla()) {
                            if (st.getTalla() == tallaEnum) {
                                tallaEncontrada = true;
                                if (st.getCantidad() < detalle.getCantidad()) {
                                    throw new Exception("Stock insuficiente para "
                                            + producto.getNombre() + " talla " + tallaStr
                                            + " (disponible: " + st.getCantidad() + ")");
                                }
                                st.setCantidad(st.getCantidad() - detalle.getCantidad());
                                break;
                            }
                        }
                        if (!tallaEncontrada) {
                            throw new Exception("Talla " + tallaStr
                                    + " no disponible para " + producto.getNombre());
                        }
                    } catch (IllegalArgumentException e) {
                        throw new Exception("Talla inválida: " + tallaStr);
                    }
                } else if (producto.getStock() < detalle.getCantidad()) {
                    throw new Exception("Stock insuficiente para: " + producto.getNombre()
                            + " (Disponible: " + producto.getStock()
                            + ", Solicitado: " + detalle.getCantidad() + ")");
                }

                total += detalle.getPrecioUnidad() * detalle.getCantidad();
                producto.setStock(producto.getStock() - detalle.getCantidad());
                em.merge(producto);
            }

            total += calcularEnvio(total);

            if (direccion.getId() == 0) {
                em.persist(direccion);
            }

            // Pago: TARJETA y TRANSFERENCIA son pasarelas simuladas — se aprueban
            // al confirmar el pedido. CONTRA_ENTREGA queda pendiente hasta la entrega.
            EstadoPago estadoPago = (metodoPago == FormaPago.CONTRA_ENTREGA)
                    ? EstadoPago.PENDIENTE
                    : EstadoPago.APROBADO;
            Pago pago = new Pago(total, LocalDate.now(), metodoPago, estadoPago);
            Pedido pedido = new Pedido(
                    generarNumeroPedido(), LocalDate.now(), total, EstadoPedido.PENDIENTE,
                    em.find(Usuario.class, usuario.getId()), direccion, null, pago
            );
            List<DetallePedido> detallesNuevos = new ArrayList<>();
            for (DetallePedido detalle : detalles) {
                DetallePedido nuevo = new DetallePedido(
                        detalle.getCantidad(),
                        detalle.getPrecioUnidad(),
                        em.find(Producto.class, detalle.getProducto().getId())
                );
                nuevo.setTalla(detalle.getTalla());
                em.persist(nuevo);
                detallesNuevos.add(nuevo);
            }
            em.persist(pedido);
            pedido.setDetalles(detallesNuevos);
            em.getTransaction().commit();
            notificarConfirmacionPedido(pedido);
            return pedido;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Error al crear pedido: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Pedido actualizarEstado(int pedidoId, EstadoPedido nuevoEstado) throws Exception {
        Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);
        if (pedido == null) {
            throw new Exception("Pedido no encontrado");
        }
        if (nuevoEstado == EstadoPedido.CANCELADO && pedido.getEstado() != EstadoPedido.CANCELADO) {
            cancelarPedido(pedidoId);
            return pedidoDAO.obtenerPorId(pedidoId);
        }
        pedido.setEstado(nuevoEstado);
        return pedidoDAO.actualizar(pedido);
    }

    public void cancelarPedido(int pedidoId) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Pedido pedido = em.find(Pedido.class, pedidoId);
            if (pedido == null) {
                throw new Exception("Pedido no encontrado");
            }
            if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
                throw new Exception("No se puede cancelar un pedido ya entregado");
            }
            if (pedido.getEstado() == EstadoPedido.CANCELADO) {
                em.getTransaction().commit();
                return;
            }
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto p = em.find(Producto.class, detalle.getProducto().getId());
                if (p == null) {
                    continue;
                }

                // Restaurar stock total
                p.setStock(p.getStock() + detalle.getCantidad());

                // Restaurar stock por talla si el detalle tenía talla y el producto la maneja
                String tallaStr = detalle.getTalla();
                if (tallaStr != null && !tallaStr.isBlank()
                        && p.getStockPorTalla() != null && !p.getStockPorTalla().isEmpty()) {
                    try {
                        itson.ecommercewebaplication.enums.Tallas tallaEnum
                                = itson.ecommercewebaplication.enums.Tallas.valueOf(tallaStr);
                        for (itson.ecommercewebaplication.models.StockTalla st : p.getStockPorTalla()) {
                            if (st.getTalla() == tallaEnum) {
                                st.setCantidad(st.getCantidad() + detalle.getCantidad());
                                break;
                            }
                        }
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                em.merge(p);
            }
            pedido.setEstado(EstadoPedido.CANCELADO);
            em.merge(pedido);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Error al cancelar pedido: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    private String generarNumeroPedido() {
        return "PED-" + System.currentTimeMillis() + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public boolean usuarioComproProducto(int usuarioId, int productoId) {
        return pedidoDAO.usuarioComproProducto(usuarioId, productoId);
    }

    public long contarComprasDeProducto(int usuarioId, int productoId) {
        return pedidoDAO.contarComprasDeProducto(usuarioId, productoId);
    }

    public List<Pedido> buscarYFiltrar(String estado, String busqueda, String orden) {
        return pedidoDAO.buscarYFiltrar(estado, busqueda, orden);
    }

    public long contarPorEstado(String estado) {
        return pedidoDAO.contarPorEstado(estado);
    }

    public double ventasDelMes() {
        return pedidoDAO.ventasDelMes();
    }

    public List<Integer> idsProductosTopVentas(int limite) {
        return pedidoDAO.idsProductosTopVentas(limite);
    }

    public List<Pedido> obtenerTodosConPago() {
        return pedidoDAO.obtenerTodosConPago();
    }

    private void notificarConfirmacionPedido(Pedido pedido) {
        try {
            Usuario u = pedido.getUsuario();
            String correo = (u != null) ? u.getCorreo() : "(sin correo)";
            System.out.println("[Notificacion email] Para: " + correo);
            System.out.println("                      Asunto: Confirmacion de pedido #" + pedido.getNumPedido());
            System.out.println("                      Total: $" + pedido.getTotal());
            System.out.println("                      Estado pedido: " + pedido.getEstado().name());
            if (pedido.getPago() != null) {
                System.out.println("                      Pago: " + pedido.getPago().getMetodo().name()
                        + " (" + pedido.getPago().getEstado().name() + ")");
            }
        } catch (Exception ignored) {
        }
    }
}
