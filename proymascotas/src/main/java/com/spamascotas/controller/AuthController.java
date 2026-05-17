package com.spamascotas.controller;

import com.spamascotas.dto.*;
import com.spamascotas.model.Usuario;
import com.spamascotas.repository.UsuarioRepository;
import com.spamascotas.security.JwtUtil;
import com.spamascotas.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(authService.autenticar(request, httpRequest));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/2fa/verify")
    public ResponseEntity<?> verificar2FA(@RequestBody VerificationRequest request) {
        if (authService.validarCodigo2FA(request.getEmail(), request.getCodigo())) {
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol().getNombreRol());
            
            return ResponseEntity.ok(new LoginResponse(
                token, 
                usuario.getId_usuario(), 
                usuario.getEmail(), 
                usuario.getUsername(), 
                usuario.getRol().getNombreRol()
            ));
        }
        return ResponseEntity.status(401).body("Código 2FA incorrecto");
    }

@PreAuthorize("hasAuthority('Administrador')")
    @PostMapping("/registrar-personal")
    public ResponseEntity<?> registrarPersonal(
            @RequestBody RegistroEmpleadoRequest request, 
            @RequestParam String rol, 
            HttpServletRequest httpRequest) {
        
        System.out.println("\n========== PROCESANDO REGISTRO DE PERSONAL ==========");
        System.out.println(">>> Parámetro 'rol' (URL): " + rol);
        System.out.println(">>> Nombres: " + request.getNombres());
        System.out.println(">>> Apellidos: " + request.getApellidos());
        System.out.println(">>> Username: " + request.getUsername());
        System.out.println(">>> Email: " + request.getEmail());

        try {
            request.setRol(rol);
            authService.crearEmpleado(request, httpRequest);
            
            System.out.println(">>> [ÉXITO] Empleado creado y auditado correctamente.");
            return ResponseEntity.ok("Personal registrado con éxito");
        } catch (Exception e) {
            System.err.println(">>> [ERROR] Falla en el proceso de registro: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            System.out.println("====================================================\n");
        }
    }

    @GetMapping("/verificar")
    public ResponseEntity<String> verificar(@RequestParam String token) {
        try {
            return ResponseEntity.ok(authService.verificarCuenta(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/2fa/setup")
    public ResponseEntity<?> setup2FA(@RequestParam String email) {
        try {
            return ResponseEntity.ok(authService.generarQR2FA(email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest request) {
        try {
            String mensaje = authService.registrarCliente(request);
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/establecer-password")
    public ResponseEntity<?> establecerPassword(@RequestBody SetPasswordRequest request) {
        System.out.println("\n========== PETICIÓN RECIBIDA EN /establecer-password ==========");
        try {
            String mensaje = authService.establecerPasswordYActivar(request);
            System.out.println(">>> Respuesta exitosa enviada al frontend.");
            return ResponseEntity.ok(mensaje);
        } catch (Exception e) {
            System.err.println(">>> [FALLO EN CONTROLADOR] Detalle: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            System.out.println("==============================================================\n");
        }
    }
    @PostMapping("/solicitar-reset-password")
    public ResponseEntity<?> solicitarReset(@RequestBody java.util.Map<String, String> request) {
        try {
            String email = request.get("email");
            authService.solicitarResetPassword(email);
            return ResponseEntity.ok("Enlace de recuperación enviado al correo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/empleado/perfil/{id}")
    public ResponseEntity<?> obtenerPerfilEmpleado(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(authService.buscarPerfilEmpleado(id));
        } catch (RuntimeException e) {
            System.err.println(">>> [ERROR] Perfil no encontrado para ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}