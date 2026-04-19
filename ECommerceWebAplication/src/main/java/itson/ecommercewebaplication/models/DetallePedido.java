package itson.ecommercewebaplication.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "precio_unidad", nullable = false)
    private double precioUnidad;

    /** Talla seleccionada por el cliente. Null si el producto no maneja tallas. */
    @Column(name = "talla", nullable = true)
    private String talla;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    public DetallePedido() {}

    public DetallePedido(int cantidad, double precioUnidad, Producto producto) {
        this.cantidad     = cantidad;
        this.precioUnidad = precioUnidad;
        this.producto     = producto;
    }

    public DetallePedido(int id, int cantidad, double precioUnidad, Producto producto) {
        this.id           = id;
        this.cantidad     = cantidad;
        this.precioUnidad = precioUnidad;
        this.producto     = producto;
    }

    public int getId()                         { return id; }
    public void setId(int id)                  { this.id = id; }
    public int getCantidad()                   { return cantidad; }
    public void setCantidad(int c)             { this.cantidad = c; }
    public double getPrecioUnidad()            { return precioUnidad; }
    public void setPrecioUnidad(double p)      { this.precioUnidad = p; }
    public String getTalla()                   { return talla; }
    public void setTalla(String talla)         { this.talla = talla; }
    public Producto getProducto()              { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}
