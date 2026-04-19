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
@Table(name = "direcciones")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String calle;

    @Column(nullable = false)
    private String ciudad;

    @Column(nullable = false)
    private String estado;

    @Column(name = "codigo_postal", nullable = false)
    private String codigoPostal;

    /** true = dirección principal del usuario (la usada por defecto en checkout). */
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean principal = false;

    // FK al usuario dueño de la dirección (nullable para que las del checkout funcionen sin usuario)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    // Constructor sin usuario (usado en checkout sin guardar al usuario)
    public Direccion() {}

    public Direccion(String calle, String ciudad, String estado, String codigoPostal) {
        this.calle = calle;
        this.ciudad = ciudad;
        this.estado = estado;
        this.codigoPostal = codigoPostal;
    }

    public Direccion(int id, String calle, String ciudad, String estado, String codigoPostal) {
        this.id = id;
        this.calle = calle;
        this.ciudad = ciudad;
        this.estado = estado;
        this.codigoPostal = codigoPostal;
    }

    public Direccion(String calle, String ciudad, String estado, String codigoPostal, Usuario usuario) {
        this.calle = calle;
        this.ciudad = ciudad;
        this.estado = estado;
        this.codigoPostal = codigoPostal;
        this.usuario = usuario;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public boolean isPrincipal()                  { return principal; }
    public void setPrincipal(boolean principal)    { this.principal = principal; }

    /** Texto resumido para mostrar en UI */
    public String getResumen() {
        return calle + ", " + ciudad + ", " + estado + " " + codigoPostal;
    }
}
