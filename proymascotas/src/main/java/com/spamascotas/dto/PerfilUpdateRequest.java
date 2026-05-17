package com.spamascotas.dto;

import lombok.Data;

@Data
public class PerfilUpdateRequest {
    private String nombre;
    private String apellido;
    private String ci;
    private String telefono;
    private String direccion;
}