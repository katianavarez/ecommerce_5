package itson.ecommercewebaplication.dto;

import java.util.List;

/**
 *
 * @author PC
 */
public class ProductoRequestDTO {

    private String nombre;
    private String descripcion;
    private double precio;
    private String imagenURL;
    private int stock;
    private List<String> tallasDisponibles;
    private String color;
    private int categoriaId;

    public ProductoRequestDTO() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public List<String> getTallasDisponibles() {
        return tallasDisponibles;
    }

    public void setTallasDisponibles(List<String> tallasDisponibles) {
        this.tallasDisponibles = tallasDisponibles;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }
}
