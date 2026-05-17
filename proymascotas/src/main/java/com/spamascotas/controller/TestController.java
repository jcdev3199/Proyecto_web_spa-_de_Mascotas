package com.spamascotas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/publico")
    public String contenidoPublico() {
        return "Este contenido lo puede ver cualquier persona sin login.";
    }

    @GetMapping("/admin")
    public String contenidoAdmin() {
        return "Bienvenido, Administrador. Aquí están los reportes financieros del Spa.";
    }

    @GetMapping("/groomer")
    public String contenidoGroomer() {
        return "Hola Groomer. Aquí tienes tu agenda de cortes y baños para hoy.";
    }
}