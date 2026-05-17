package com.spamascotas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;
import lombok.Data;

@Data
public class GroomerDTO {
    private String telefono;
    private String especialidad;
    private String turno;
    
    @JsonProperty("capacidad_simultanea")
    private Integer capacidadSimultanea;

    @JsonProperty("hora_entrada")
    private LocalTime horaEntrada;

    @JsonProperty("hora_salida")
    private LocalTime horaSalida;
}