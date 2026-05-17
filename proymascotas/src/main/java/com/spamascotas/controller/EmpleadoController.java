package com.spamascotas.controller;

import com.spamascotas.dto.PerfilEmpleadoDTO;
import com.spamascotas.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empleado")
public class EmpleadoController {

    @Autowired
    private AuthService authService;

    @GetMapping("/perfil/{id}")
    public ResponseEntity<?> obtenerPerfilEmpleado(@PathVariable Integer id) {
        try {
            PerfilEmpleadoDTO perfil = authService.buscarPerfilEmpleado(id);
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}