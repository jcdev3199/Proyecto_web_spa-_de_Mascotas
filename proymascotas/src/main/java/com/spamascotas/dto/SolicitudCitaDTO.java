package com.spamascotas.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SolicitudCitaDTO {
    private Integer id_mascota;
    private Integer id_groomer;
    private Integer id_servicio;
    private LocalDate fecha;
    private String horaInicio;
}