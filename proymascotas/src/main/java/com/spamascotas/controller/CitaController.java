package com.spamascotas.controller;

import com.spamascotas.dto.CitaRequest;
import com.spamascotas.model.Cita;
import com.spamascotas.service.CitaService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @PostMapping("/solicitar")
    public ResponseEntity<?> registrarCita(@RequestBody CitaRequest request) {
        System.out.println("\n========== [DEBUG BACKEND - CONTROLLER] ==========");
        System.out.println("📥 Datos crudos recibidos en la petición:");
        System.out.println(" -> ID Mascota: " + request.getIdMascota());
        System.out.println(" -> ID Groomer: " + request.getIdGroomer());
        System.out.println(" -> ID Servicio: " + request.getIdServicio());
        System.out.println(" -> Fecha: " + request.getFecha());
        System.out.println(" -> Hora Inicio: " + request.getHoraInicio());
        System.out.println("==================================================\n");

        try {
            Cita nueva = citaService.solicitarCita(request);
            System.out.println("✨ [DEBUG BACKEND] Cita guardada con éxito. ID: " + nueva.getId_cita());
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            System.err.println("❌ [DEBUG BACKEND - ERROR] Falló la validación o persistencia:");
            System.err.println(" ➔ Mensaje: " + e.getMessage());
            e.printStackTrace(); 
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/groomers")
    public ResponseEntity<List<com.spamascotas.model.Groomer>> getGroomers() {
        return ResponseEntity.ok(citaService.listarGroomers());
    }

    @GetMapping("/servicios")
    public ResponseEntity<List<com.spamascotas.model.Servicio>> getServicios() {
        return ResponseEntity.ok(citaService.listarServicios());
    }
}