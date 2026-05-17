package com.spamascotas.controller;

import com.spamascotas.dto.PerfilUpdateRequest;
import com.spamascotas.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cliente")
@CrossOrigin(origins = "http://localhost:5173")
public class ClienteController {

    @Autowired
    private AuthService authService;

    @PutMapping("/perfil/{idUsuario}")
    public String editarPerfil(@PathVariable Integer idUsuario, 
                            @RequestBody PerfilUpdateRequest request, 
                            HttpServletRequest httpRequest) {
        
        System.out.println("\n--- REPORTE DE ACTUALIZACIÓN (BACKEND) ---");
        System.out.println("ID RECIBIDO: " + idUsuario);
        System.out.println("DATOS NUEVOS: " + request.getNombre() + " " + request.getApellido());
        System.out.println("TELÉFONO: " + request.getTelefono());
        System.out.println("------------------------------------------\n");
        if (idUsuario == null) {
        throw new RuntimeException("Error: El ID de usuario llegó nulo al servidor.");
         }
        return authService.actualizarPerfilCliente(idUsuario, request, httpRequest);
    }

    @GetMapping("/perfil/{idUsuario}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(authService.buscarDatosCliente(idUsuario));
    }
}