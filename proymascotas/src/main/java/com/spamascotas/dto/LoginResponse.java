package com.spamascotas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Integer id_usuario;
    private String email;
    private String nombre;
    private String rol;
}