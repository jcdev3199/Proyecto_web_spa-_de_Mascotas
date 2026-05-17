package com.spamascotas.dto;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private Integer idUsuario;
    private String username;
    private String estado;
    private String newPassword;
    private GroomerDTO groomer;
    private ClienteDTO cliente;
}