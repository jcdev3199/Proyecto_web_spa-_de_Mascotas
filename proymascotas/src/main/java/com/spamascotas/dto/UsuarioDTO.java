package com.spamascotas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    
    private Integer id_usuario;
    private String username;
    private String email;
    private String nombreRol;
    private String estado;
    private Integer id_rol;

    private GroomerDTO groomer; 
    private ClienteDTO cliente;
}
