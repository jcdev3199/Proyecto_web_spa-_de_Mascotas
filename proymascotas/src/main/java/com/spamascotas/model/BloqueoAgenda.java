package com.spamascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "bloqueos_agenda")
@Data
public class BloqueoAgenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_bloqueo;

    @Column(name = "id_groomer")
    private Integer idGroomer;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private String motivo; 

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;
}