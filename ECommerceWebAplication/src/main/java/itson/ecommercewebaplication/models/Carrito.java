package itson.ecommercewebaplication.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Carrito persistente asociado a un usuario logueado. Cada cliente tiene
 * un único carrito en BD que sobrevive entre sesiones, separado del
 * carrito en localStorage que usan los visitantes invitados.
 * 
 * Los items se modelan con {@link DetallePedido} (la misma entidad que
 * se reutiliza al confirmar la compra), pero conviven con un FK distinto
 * ({@code carrito_id}) que se queda en null una vez el pedido se concreta.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@Entity
@Table(name = "carritos")
public class Carrito{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "carrito_id")
    private List<DetallePedido> detalles;
    
    @Column(nullable = false)
    private double total;

    public Carrito() {
    }

    public Carrito(int id, Usuario usuario, List<DetallePedido> detalles, double total) {
        this.id = id;
        this.usuario = usuario;
        this.detalles = detalles;
        this.total = total;
    }

    public Carrito(Usuario usuario, List<DetallePedido> detalles, double total) {
        this.usuario = usuario;
        this.detalles = detalles;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
    
    
}
