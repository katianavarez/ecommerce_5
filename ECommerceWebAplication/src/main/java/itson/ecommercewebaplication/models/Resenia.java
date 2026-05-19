package itson.ecommercewebaplication.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Reseña que un cliente deja sobre un producto comprado. Lleva una
 * calificación de 1 a 5 estrellas, un comentario libre y la fecha en que
 * se escribió. La regla del negocio permite solo una reseña por (usuario,
 * producto), y exige que el usuario haya comprado el producto antes.
 * 
 * El nombre de la tabla es {@code resenias} (sin ñ) por compatibilidad
 * de charset entre distintas instalaciones de MySQL.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@Entity
@Table(name = "resenias")
public class Resenia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private int calificacion;
    
    @Column(columnDefinition = "TEXT")
    private String comentario;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Resenia() {
    }

    public Resenia(int id, int calificacion, String comentario, LocalDate fecha, Producto producto, Usuario usuario) {
        this.id = id;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fecha = fecha;
        this.producto = producto;
        this.usuario = usuario;
    }

    public Resenia(int calificacion, String comentario, LocalDate fecha, Producto producto, Usuario usuario) {
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fecha = fecha;
        this.producto = producto;
        this.usuario = usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
