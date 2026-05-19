package itson.ecommercewebaplication.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.text.Normalizer;
import java.util.List;

/**
 * Categoría a la que pertenece una prenda dentro del catálogo
 * (Vestidos, Blusas, Conjuntos, etc.). Cada producto se asocia con
 * exactamente una categoría y el cliente puede filtrar el catálogo por
 * ella desde {@code productos.jsp}.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;

    public Categoria() {
    }

    public Categoria(int id, String nombre, List<Producto> productos) {
        this.id = id;
        this.nombre = nombre;
        this.productos = productos;
    }

    public Categoria(String nombre, List<Producto> productos) {
        this.nombre = nombre;
        this.productos = productos;
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

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    /**
     * Devuelve una versión del nombre sin acentos ni símbolos, en minúsculas,
     * apta para usar como nombre de archivo o como clase CSS. Por ejemplo,
     * "Accesorios" → "accesorios" y "Saco / Blazer" → "sacoblazer".
     */
    @Transient
    public String getSlug() {
        if (nombre == null) return "";
        String normalizado = Normalizer.normalize(nombre, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalizado.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}
