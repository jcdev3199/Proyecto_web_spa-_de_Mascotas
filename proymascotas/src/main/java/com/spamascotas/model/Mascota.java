package com.spamascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "mascotas")
@Data
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota")
    private Integer id_mascota;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String especie;

    @Column(length = 50)
    private String raza;

    @Column(nullable = false, length = 255)
    private String tamano;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(columnDefinition = "TEXT")
    private String alergias;

    @Column(columnDefinition = "TEXT")
    private String vacunas;

    @Column(columnDefinition = "TEXT")
    private String restricciones;

    @ManyToOne
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    private Cliente cliente;
}