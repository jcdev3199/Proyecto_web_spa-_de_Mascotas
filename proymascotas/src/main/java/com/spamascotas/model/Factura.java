package com.spamascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "facturas")
@Data
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_factura;

    @Column(name = "id_cita", unique = true)
    private Integer idCita;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente; 

    @Column(name = "numero_factura", nullable = false, unique = true, length = 50)
    private String numeroFactura; 
    @Column(nullable = false)
    private LocalDate fecha_emision;

    @Column(nullable = false)
    private Double subtotal; 
    @Column(nullable = false)
    private Double impuestos; 
    @Column(nullable = false)
    private Double total; 
    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago; 

    @Column(name = "monto_total", nullable = false)
    private Double montoTotal;

    public Integer getId_cliente() { return this.idCliente; }
    public void setId_cliente(Integer id_cliente) { this.idCliente = id_cliente; }
    public String getNumero_factura() { return this.numeroFactura; }
    public void setNumero_factura(String numero_factura) { this.numeroFactura = numero_factura; }
}