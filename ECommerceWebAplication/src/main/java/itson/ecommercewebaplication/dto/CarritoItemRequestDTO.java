package itson.ecommercewebaplication.dto;

/**
 *
 * @author PC
 */
public class CarritoItemRequestDTO {

    private int productoId;
    private int cantidad;

    public CarritoItemRequestDTO() {
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
