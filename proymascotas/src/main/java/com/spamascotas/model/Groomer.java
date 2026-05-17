package com.spamascotas.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.time.LocalTime;

@Entity
@Table(name = "groomers")
@Data
public class Groomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_groomer;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(length = 100)
    private String especialidad;

    @Column
    private String turno;

    @Column(name = "capacidad_simultanea")
    private Integer capacidad_simultanea = 1;

    @Column(name = "hora_entrada")
    private LocalTime hora_entrada;

    @Column(name = "hora_salida")
    private LocalTime hora_salida;
}