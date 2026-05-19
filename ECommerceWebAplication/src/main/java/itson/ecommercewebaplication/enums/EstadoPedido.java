package itson.ecommercewebaplication.enums;

/**
 * Ciclo de vida de un pedido. Un pedido recién creado entra como PENDIENTE;
 * el admin lo va moviendo a ENVIADO y luego a ENTREGADO desde el panel.
 * CANCELADO es una salida posible mientras el pedido aún no esté entregado
 * (lo puede hacer tanto el cliente desde su cuenta como el admin).
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public enum EstadoPedido {
    PENDIENTE,
    ENVIADO,
    ENTREGADO,
    CANCELADO
}
