package itson.ecommercewebaplication.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(length = 13)
    private String rfc;

    @Column(length = 20)
    private String telefono;

    private String correo;

    private String direccion;

    /** Soft delete: false = proveedor archivado. */
    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "proveedor")
    private List<Producto> productos;

    public Proveedor() {}

    public Proveedor(String nombre, String rfc, String telefono, String correo, String direccion) {
        this.nombre = nombre;
        this.rfc = rfc;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }
    public String getNombre()                { return nombre; }
    public void setNombre(String nombre)     { this.nombre = nombre; }
    public String getRfc()                   { return rfc; }
    public void setRfc(String rfc)           { this.rfc = rfc; }
    public String getTelefono()              { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo()                { return correo; }
    public void setCorreo(String correo)     { this.correo = correo; }
    public String getDireccion()             { return direccion; }
    public void setDireccion(String d)       { this.direccion = d; }
    public boolean isActivo()                { return activo; }
    public void setActivo(boolean activo)    { this.activo = activo; }
    public List<Producto> getProductos()     { return productos; }
    public void setProductos(List<Producto> p) { this.productos = p; }
}
