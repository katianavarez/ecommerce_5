package itson.ecommercewebaplication.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import itson.ecommercewebaplication.enums.Rol;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Entidad que representa a un usuario del sistema, ya sea cliente o
 * administrador. Mantiene los datos de identidad (nombre, correo, teléfono),
 * el rol que define el acceso al panel admin y las relaciones con sus
 * pedidos, carrito, dirección principal y reseñas.
 * 
 * El campo {@code activo} permite hacer baja lógica (soft delete) sin
 * romper las FK históricas que apuntan a este usuario desde Pedido.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    // El campo Java se llama 'contrasena' sin ñ y la columna SQL también es ASCII puro
    // para evitar problemas de portabilidad con MySQL (charset/collation y lower_case_table_names).
    // @JsonIgnore: defensa en profundidad para que el hash BCrypt nunca pueda salir
    // serializado al cliente aunque por error se devuelva un Usuario entero en una respuesta.
    @JsonIgnore
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    // Teléfono opcional: el registro permite dejarlo en blanco.
    @Column(nullable = true)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean activo = true;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "direccion_id", referencedColumnName = "id")
    private Direccion direccion;

    @OneToMany(mappedBy = "usuario")
    private List<Pedido> historialPedidos;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Carrito carrito;

    @OneToMany(mappedBy = "usuario")
    private List<Resenia> resenias;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String correo, String contrasena, String telefono, Rol rol, Direccion direccion, List<Pedido> historialPedidos, Carrito carrito, List<Resenia> resenias) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.rol = rol;
        this.direccion = direccion;
        this.historialPedidos = historialPedidos;
        this.carrito = carrito;
        this.resenias = resenias;
    }

    public Usuario(String nombre, String correo, String contrasena, String telefono, Rol rol, Direccion direccion, List<Pedido> historialPedidos, Carrito carrito, List<Resenia> resenias) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.rol = rol;
        this.direccion = direccion;
        this.historialPedidos = historialPedidos;
        this.carrito = carrito;
        this.resenias = resenias;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public List<Pedido> getHistorialPedidos() {
        return historialPedidos;
    }

    public void setHistorialPedidos(List<Pedido> historialPedidos) {
        this.historialPedidos = historialPedidos;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public List<Resenia> getResenias() {
        return resenias;
    }

    public void setResenias(List<Resenia> resenias) {
        this.resenias = resenias;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
