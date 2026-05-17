package com.spamascotas.dto;

import lombok.Data;

@Data
public class PerfilEmpleadoDTO {
    private String nombres;
    private String apellidos;
    private String telefono;
    private String turno;
    private String horaEntrada;
    private String horaSalida;
    private String rol;
    private String especialidad;
    private Integer capacidadSimultanea;
}