package com.spamascotas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "fichas_grooming")
@Data
public class FichaGrooming {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_ficha;

    @Column(name = "id_cita", nullable = false, unique = true)
    private Integer idCita;

    @Column(name = "estado_ingreso", columnDefinition = "TEXT")
    private String estadoIngreso; 

    @Column(length = 100)
    private String temperamento;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(columnDefinition = "TEXT")
    private String recomendaciones;

    @Column(name = "foto_antes", columnDefinition = "LONGTEXT")
    private String fotoAntes;

    @Column(name = "foto_despues", columnDefinition = "LONGTEXT")
    private String fotoDespues;
}