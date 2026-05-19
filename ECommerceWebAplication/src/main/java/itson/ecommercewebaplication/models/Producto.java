package itson.ecommercewebaplication.models;

import itson.ecommercewebaplication.enums.Colores;
import itson.ecommercewebaplication.enums.Tallas;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Prenda o accesorio del catálogo. Concentra la información que se muestra
 * tanto en la lista del cliente como en el panel admin: nombre, descripción,
 * precio, imagen, categoría, proveedor opcional, color y stock.
 * 
 * El inventario se maneja de dos formas según el tipo de producto: las
 * prendas con tallas usan {@code stockPorTalla} (una entrada por talla),
 * mientras que los accesorios sin talla guardan el inventario únicamente
 * en el campo {@code stock}. {@link #recalcularStockTotal()} mantiene la
 * suma consistente con las cantidades por talla.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private double precio;

    // length=2048: las URLs de imágenes externas (ej. CDNs con tokens, búsquedas
    // de Brave/Google) pueden pasar fácilmente los 255 chars del default de
    // Hibernate y romper el INSERT con "Data too long for column".
    @Column(name = "imagen_url", nullable = false, length = 2048)
    private String imagenURL;

    @Column(nullable = false)
    private int stock;

    @ElementCollection
    @CollectionTable(name = "producto_tallas", joinColumns = @JoinColumn(name = "producto_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "talla")
    private List<Tallas> tallasDisponibles;

    /**
     * Stock distribuido por talla.
     */
    @ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
    @CollectionTable(name = "producto_stock_talla", joinColumns = @JoinColumn(name = "producto_id"))
    private List<StockTalla> stockPorTalla;

    /**
     * Color guardado como String en BD para compatibilidad. Se guarda el name()
     * del enum Colores (ej: "NEGRO", "AZUL"). Usa getColorEnum() para obtener
     * el objeto Colores.
     */
    @Column(name = "color")
    private String color;

    /**
     * Soft delete: false = producto archivado (no visible en catálogo).
     */
    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /**
     * Proveedor opcional — null si el producto no tiene proveedor asociado.
     */
    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @OneToMany(mappedBy = "producto")
    private List<Resenia> resenias;

    public Producto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setDescripcion(String d) {
        this.descripcion = d;
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

    public void setImagenURL(String url) {
        this.imagenURL = url;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public List<Tallas> getTallasDisponibles() {
        return tallasDisponibles;
    }

    public void setTallasDisponibles(List<Tallas> t) {
        this.tallasDisponibles = t;
    }

    public List<StockTalla> getStockPorTalla() {
        return stockPorTalla;
    }

    public void setStockPorTalla(List<StockTalla> s) {
        this.stockPorTalla = s;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria c) {
        this.categoria = c;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public List<Resenia> getResenias() {
        return resenias;
    }

    public void setResenias(List<Resenia> r) {
        this.resenias = r;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setColorEnum(Colores colorEnum) {
        this.color = (colorEnum != null) ? colorEnum.name() : null;
    }

    @Transient
    public Colores getColorEnum() {
        if (color == null || color.isBlank()) {
            return null;
        }
        try {
            return Colores.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // valor legacy en BD que no coincide con el enum
        }
    }

    @Transient
    public String getColorNombre() {
        Colores c = getColorEnum();
        if (c != null) {
            return c.getNombre();
        }
        return (color != null && !color.isBlank()) ? color : "Sin color";
    }

    @Transient
    public String getColorHex() {
        Colores c = getColorEnum();
        return c != null ? c.getHex() : "#CCCCCC";
    }

    /**
     * Recalcula el stock total como la suma del stock por talla y, de paso,
     * regenera la lista de tallas disponibles excluyendo aquellas con cero
     * unidades. Solo aplica si el producto maneja tallas; para accesorios
     * se conserva el {@code stock} tal cual.
     */
    public void recalcularStockTotal() {
        if (stockPorTalla != null && !stockPorTalla.isEmpty()) {
            this.stock = stockPorTalla.stream().mapToInt(StockTalla::getCantidad).sum();
            List<Tallas> disponibles = new ArrayList<>();
            for (StockTalla st : stockPorTalla) {
                if (st.getCantidad() > 0) {
                    disponibles.add(st.getTalla());
                }
            }
            this.tallasDisponibles = disponibles;
        }
    }

    /**
     * Devuelve la cantidad disponible de una talla concreta, o 0 si el
     * producto no la maneja. Lo usan las JSP de catálogo y producto-detalle
     * para mostrar "Agotado" en las tallas sin existencia.
     */
    @Transient
    public int getStockDeTalla(Tallas talla) {
        if (stockPorTalla == null) {
            return 0;
        }
        for (StockTalla st : stockPorTalla) {
            if (st.getTalla() == talla) {
                return st.getCantidad();
            }
        }
        return 0;
    }
}
