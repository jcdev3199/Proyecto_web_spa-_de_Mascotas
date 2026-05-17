package com.spamascotas.dto;

import lombok.Data;

@Data
public class SetPasswordRequest {
    private String token;
    private String password;
}