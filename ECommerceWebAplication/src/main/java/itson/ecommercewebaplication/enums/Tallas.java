package itson.ecommercewebaplication.enums;

/**
 * Tallas que se pueden asignar a las prendas. Los accesorios (bolsas, etc.)
 * no llevan talla, así que su lista de StockTalla queda vacía y el stock se
 * maneja como un único contador en {@code Producto.stock}.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public enum Tallas {
    XS,
    S,
    M,
    L,
    XL
}
