package itson.ecommercewebaplication.enums;

/**
 * Métodos de pago que la tienda acepta en el checkout (todos simulados,
 * no se conecta a un proveedor real). TARJETA se aprueba al instante;
 * TRANSFERENCIA y CONTRA_ENTREGA dejan el pago en estado PENDIENTE hasta
 * verificación manual.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public enum FormaPago {
    TARJETA,
    TRANSFERENCIA,
    CONTRA_ENTREGA
}
