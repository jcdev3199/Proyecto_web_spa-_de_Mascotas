package com.spamascotas.dto;

import java.time.LocalTime;

import lombok.Data;

@Data
public class PerfilUpdateRequest {
    private String nombre;
    private String apellido;
    private String ci;
    private String telefono;
    private String especialidad;
    private String direccion;
    private String turno;
    private Integer capacidadSimultanea;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
}