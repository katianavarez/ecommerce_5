package itson.ecommercewebaplication.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Dirección de envío. Un usuario puede tener varias guardadas en su cuenta,
 * pero solo una marcada como principal (la que se selecciona por defecto
 * en el checkout). El FK al usuario es nullable porque las direcciones que
 * el cliente teclea en el checkout sin guardarlas en su perfil también se
 * persisten aquí, asociadas al pedido pero no al usuario.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
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

    /**
     * true = dirección principal del usuario (la usada por defecto en
     * checkout).
     */
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean principal = false;

    // FK al usuario dueño de la dirección (nullable para que las del checkout funcionen sin usuario)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    // Constructor sin usuario (usado en checkout sin guardar al usuario)
    public Direccion() {
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    /**
     * Concatena los cuatro campos en una sola línea legible para mostrar
     * en las tarjetas de "Mis direcciones" del panel de cuenta y en el
     * resumen del pedido confirmado.
     */
    public String getResumen() {
        return calle + ", " + ciudad + ", " + estado + " " + codigoPostal;
    }
}
