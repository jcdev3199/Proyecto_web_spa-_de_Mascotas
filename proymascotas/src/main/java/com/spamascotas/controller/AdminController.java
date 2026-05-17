package com.spamascotas.controller;

import com.spamascotas.dto.RegistroEmpleadoRequest;
import com.spamascotas.dto.UserUpdateRequest;
import com.spamascotas.dto.UsuarioDTO;
import com.spamascotas.model.Auditoria;
import com.spamascotas.repository.AuditoriaRepository;
import com.spamascotas.service.AuthService;
import com.spamascotas.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired 
    private AuthService authService;

    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasAuthority('Administrador')")
    @GetMapping("/logs")
    public List<Auditoria> obtenerLogs() {
        return auditoriaRepository.findAllByOrderByFechaHoraDesc();
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @GetMapping("/usuarios/lista")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        System.out.println(">>> [ADMIN] Generando lista completa de usuarios con detalles de Groomer/Cliente");
        
        List<UsuarioDTO> lista = usuarioService.listarUsuariosConDetalles();
            
        return ResponseEntity.ok(lista);
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @PostMapping("/empleados/nuevo")
    public ResponseEntity<?> registrarEmpleado(@RequestBody RegistroEmpleadoRequest request, HttpServletRequest httpRequest) {
        System.out.println(">>> [ADMIN] Registrando nuevo empleado: " + request.getEmail());
        authService.crearEmpleado(request, httpRequest);
        return ResponseEntity.ok("Empleado registrado correctamente");
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @PutMapping("/usuarios/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody UserUpdateRequest request) {
        System.out.println(">>> [DEBUG CONTROLLER] Petición PUT recibida para ID: " + id);
        try {
            request.setIdUsuario(id);
            usuarioService.actualizarUsuarioCompleto(request);
            return ResponseEntity.ok().body("{\"message\": \"Sincronización exitosa\"}");
        } catch (Exception e) {
            System.err.println(">>> [DEBUG CONTROLLER] ERROR FATAL: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }
}