package com.spamascotas.dto;

import lombok.Data;

@Data
public class RegistroRequest {
    private String email;
    private String password;
    private String username;
    
    private String nombre;
    private String apellido;
    private String ci;
    private String telefono;
    private String direccion;
    
    private String especialidad;
    private String turno;

    private String horaEntrada;
    private String horaSalida;
}