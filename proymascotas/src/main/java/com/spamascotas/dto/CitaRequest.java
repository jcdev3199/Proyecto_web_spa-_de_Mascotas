package com.spamascotas.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CitaRequest {
    private Integer idMascota;
    private Integer idGroomer;
    private Integer idServicio;
    private LocalDate fecha;
    private LocalTime horaInicio;
}