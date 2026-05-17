package com.spamascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Entity
@Table(name = "recepcionistas")
@Data
public class Recepcionista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_recepcionista;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column
    private String turno;

    @Column(name = "hora_entrada")
    private LocalTime hora_entrada;

    @Column(name = "hora_salida")
    private LocalTime hora_salida;
}