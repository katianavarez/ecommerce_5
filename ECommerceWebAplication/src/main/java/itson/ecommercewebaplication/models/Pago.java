/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.ecommercewebaplication.models;

import itson.ecommercewebaplication.enums.EstadoPago;
import itson.ecommercewebaplication.enums.FormaPago;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;


@Entity
@Table (name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private double monto;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPago metodo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    public Pago() {
    }

    public Pago(int id, double monto, LocalDate fecha, FormaPago metodo, EstadoPago estado) {
        this.id = id;
        this.monto = monto;
        this.fecha = fecha;
        this.metodo = metodo;
        this.estado = estado;
    }

    public Pago(double monto, LocalDate fecha, FormaPago metodo, EstadoPago estado) {
        this.monto = monto;
        this.fecha = fecha;
        this.metodo = metodo;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public FormaPago getMetodo() {
        return metodo;
    }

    public void setMetodo(FormaPago metodo) {
        this.metodo = metodo;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }
    
    
}
