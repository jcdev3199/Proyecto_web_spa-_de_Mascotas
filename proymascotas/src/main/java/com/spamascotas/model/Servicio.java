package com.spamascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "servicios")
@Data
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_servicio;

    @Column(name = "nombre_servicio", nullable = false)
    private String nombreServicio;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "duracion_base_minutos", nullable = false)
    private Integer duracionBaseMinutos;

    @Column(name = "precio_base", nullable = false)
    private BigDecimal precioBase;
}