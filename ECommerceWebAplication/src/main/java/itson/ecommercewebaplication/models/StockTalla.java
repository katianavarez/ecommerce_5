package itson.ecommercewebaplication.models;

import itson.ecommercewebaplication.enums.Tallas;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 *
 * @author PC
 */
@Embeddable
public class StockTalla {

    @Enumerated(EnumType.STRING)
    @Column(name = "talla", nullable = false)
    private Tallas talla;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    public StockTalla() {}

    public StockTalla(Tallas talla, int cantidad) {
        this.talla    = talla;
        this.cantidad = cantidad;
    }

    public Tallas getTalla()    { return talla; }
    public void setTalla(Tallas talla) { this.talla = talla; }
    public int getCantidad()    { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
