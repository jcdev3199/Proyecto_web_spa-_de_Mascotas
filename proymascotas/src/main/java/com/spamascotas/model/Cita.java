package com.spamascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
@Data
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_cita;

    @Column(name = "id_mascota", nullable = false)
    private Integer idMascota;

    @ManyToOne
    @JoinColumn(name = "id_groomer", nullable = false)
    private Groomer groomer;

    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin_estimada", nullable = false)
    private LocalTime horaFinEstimada;

    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "id_mascota", insertable = false, updatable = false)
    private Mascota mascota;

    public Integer getIdGroomer() {
        return this.groomer != null ? this.groomer.getId_groomer() : null;
    }

    public Integer getId_groomer() {
        return this.groomer != null ? this.groomer.getId_groomer() : null;
    }

    public Integer getIdServicio() {
        return this.servicio != null ? this.servicio.getId_servicio() : null;
    }

    public Integer getId_servicio() {
        return this.servicio != null ? this.servicio.getId_servicio() : null;
    }

    public Integer getId_mascota() {
        return this.idMascota;
    }

    public void setId_mascota(Integer id_mascota) {
        this.idMascota = id_mascota;
    }
}