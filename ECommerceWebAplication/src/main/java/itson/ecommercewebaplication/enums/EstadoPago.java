package itson.ecommercewebaplication.enums;

/**
 * Estados por los que pasa un pago dentro del flujo de checkout.
 * APROBADO solo se asigna directo en la pasarela simulada cuando el método
 * es TARJETA; TRANSFERENCIA y CONTRA_ENTREGA arrancan en PENDIENTE hasta
 * que el admin confirma el cobro.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public enum EstadoPago {
    PENDIENTE,
    APROBADO,
    RECHAZADO
}
