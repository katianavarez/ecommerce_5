package itson.ecommercewebaplication.models;

import itson.ecommercewebaplication.enums.Tallas;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Cantidad disponible de una prenda en una talla específica. Se guarda
 * como tipo embebido dentro de la tabla {@code producto_stock_talla},
 * que es lo que permite que un mismo Producto tenga inventario distinto
 * por cada talla (ej. XS=2, M=10, L=4).
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@Embeddable
public class StockTalla {

    @Enumerated(EnumType.STRING)
    @Column(name = "talla", nullable = false)
    private Tallas talla;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    public StockTalla() {
    }

    public StockTalla(Tallas talla, int cantidad) {
        this.talla = talla;
        this.cantidad = cantidad;
    }

    public Tallas getTalla() {
        return talla;
    }

    public void setTalla(Tallas talla) {
        this.talla = talla;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
