package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.PedidoDAO;
import itson.ecommercewebaplication.enums.EstadoPago;
import itson.ecommercewebaplication.enums.EstadoPedido;
import itson.ecommercewebaplication.enums.FormaPago;
import itson.ecommercewebaplication.models.*;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Lógica de negocio de pedidos. Es la parte más delicada del sistema porque
 * coordina varias cosas en una sola transacción al confirmar una compra:
 * valida el stock (por talla cuando aplica), lo descuenta, calcula el total
 * con envío, crea el pago según la forma elegida y genera el número único
 * de pedido. También maneja la cancelación, que devuelve el stock reservado.
 * El descuento de stock usa un bloqueo pesimista para que dos compras
 * simultáneas del mismo producto no terminen vendiendo más unidades de las
 * disponibles.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
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

    /**
     * Crea un pedido completo dentro de una sola transacción: recorre cada
     * detalle bloqueando el producto, valida que haya stock (por talla si la
     * prenda usa tallas) y lo descuenta, suma el total más el envío, registra
     * el pago y persiste el pedido con su número único. Si algo falla, se hace
     * rollback y no queda nada a medias.
     *
     * @param usuario   cliente que realiza la compra
     * @param direccion dirección de envío del pedido
     * @param detalles  líneas del carrito que se van a comprar
     * @param metodoPago forma de pago elegida en el checkout
     * @return el pedido ya persistido, con su número generado
     * @throws Exception si falta algún dato, no hay stock o falla la transacción
     */
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
                // PESSIMISTIC_WRITE serializa la lectura+escritura del stock entre
                // pedidos concurrentes para evitar vender más unidades de las disponibles.
                Producto producto = em.find(Producto.class, detalle.getProducto().getId(),
                        LockModeType.PESSIMISTIC_WRITE);
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

            // Solo TARJETA se aprueba al instante (pasarela simulada).
            // TRANSFERENCIA queda PENDIENTE hasta que se verifique el comprobante,
            // y CONTRA_ENTREGA queda PENDIENTE hasta la entrega.
            EstadoPago estadoPago = (metodoPago == FormaPago.TARJETA)
                    ? EstadoPago.APROBADO
                    : EstadoPago.PENDIENTE;
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

    /**
     * Cambia el estado de un pedido (lo usa el admin desde el panel). Si el
     * nuevo estado es CANCELADO, delega en {@link #cancelarPedido(int)} para
     * que además se restaure el stock; cualquier otro cambio solo actualiza
     * el estado.
     *
     * @throws Exception si el pedido no existe
     */
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

    /**
     * Cancela un pedido y devuelve a inventario las unidades que tenía
     * reservadas (tanto el stock total como el stock por talla). No permite
     * cancelar un pedido ya entregado; si ya estaba cancelado, no hace nada.
     *
     * @throws Exception si el pedido no existe o ya fue entregado
     */
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

    /**
     * Arma el número de pedido visible para el cliente combinando un sello
     * de tiempo con un fragmento de UUID, p. ej. "PED-1716123456789-A1B2C3D4".
     * La combinación hace prácticamente imposible que dos pedidos colisionen.
     */
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
        if (estado == null || estado.isBlank() || "TODOS".equalsIgnoreCase(estado)) {
            return pedidoDAO.contarTotal();
        }
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
