package itson.ecommercewebaplication.models;

import itson.ecommercewebaplication.enums.EstadoPedido;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;

/**
 * Pedido confirmado por un cliente. Reúne el usuario, la dirección de envío,
 * el pago y la lista de detalles (productos comprados). Cada pedido tiene un
 * número único legible ({@code numPedido}, ej. "PED-1716123456-A1B2C3D4")
 * que se muestra al cliente en la pantalla de confirmación.
 * 
 * El estado se mueve dentro de {@link EstadoPedido} a lo largo del ciclo de
 * vida (PENDIENTE → ENVIADO → ENTREGADO, o CANCELADO si se aborta antes).
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@Entity
@Table(name = "pedidos")
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "num_pedido", nullable = false, unique = true)
    private String numPedido;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @Column(nullable = false)
    private double total;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "direccion_envio_id", nullable = false)
    private Direccion direccionEnvio;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pedido_id")
    private List<DetallePedido> detalles;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pago_id", referencedColumnName = "id")
    private Pago pago;

    public Pedido() {
    }

    public Pedido(int id, String numPedido, LocalDate fecha, double total, EstadoPedido estado, Usuario usuario, Direccion direccionEnvio, List<DetallePedido> detalles, Pago pago) {
        this.id = id;
        this.numPedido = numPedido;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
        this.usuario = usuario;
        this.direccionEnvio = direccionEnvio;
        this.detalles = detalles;
        this.pago = pago;
    }

    public Pedido(String numPedido, LocalDate fecha, double total, EstadoPedido estado, Usuario usuario, Direccion direccionEnvio, List<DetallePedido> detalles, Pago pago) {
        this.numPedido = numPedido;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
        this.usuario = usuario;
        this.direccionEnvio = direccionEnvio;
        this.detalles = detalles;
        this.pago = pago;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumPedido() {
        return numPedido;
    }

    public void setNumPedido(String numPedido) {
        this.numPedido = numPedido;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Direccion getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(Direccion direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }
    
    
}
