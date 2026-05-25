package com.spamascotas.controller;

import com.spamascotas.dto.PerfilEmpleadoDTO;
import com.spamascotas.dto.PerfilUpdateRequest;
import com.spamascotas.service.AuthService;
import com.spamascotas.service.UsuarioService;
import com.spamascotas.model.Cita;
import com.spamascotas.model.Factura;
import com.spamascotas.model.BloqueoAgenda;
import com.spamascotas.model.Groomer;
import com.spamascotas.model.Mascota;
import com.spamascotas.repository.CitaRepository;
import com.spamascotas.repository.FacturaRepository;
import com.spamascotas.repository.BloqueoAgendaRepository;
import com.spamascotas.repository.GroomerRepository;
import com.spamascotas.repository.MascotaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/empleado")
@CrossOrigin(origins = "http://localhost:5173")
public class EmpleadoController {

    @Autowired private AuthService authService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private CitaRepository citaRepository;
    @Autowired private FacturaRepository facturaRepository;
    @Autowired private BloqueoAgendaRepository bloqueoAgendaRepository;
    @Autowired private GroomerRepository groomerRepository;
    @Autowired private MascotaRepository mascotaRepository;

    private final String uploadDir = "uploads/vacunas";

    @GetMapping("/perfil/{id}")
    public ResponseEntity<?> obtenerPerfilEmpleado(@PathVariable Integer id) {
        try {
            PerfilEmpleadoDTO perfil = authService.buscarPerfilEmpleado(id);
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/perfil/{id}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Long id, @RequestBody PerfilUpdateRequest request) {
        try {
            usuarioService.actualizarDatosEmpleado(id, request);
            return ResponseEntity.ok().body("{\"message\": \"Perfil actualizado con éxito\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar: " + e.getMessage());
        }
    }

    @GetMapping("/groomers")
    public ResponseEntity<List<Groomer>> obtenerGroomers() {
        return ResponseEntity.ok(groomerRepository.findAll());
    }

    @GetMapping("/citas/todas")
    public ResponseEntity<List<Cita>> listarTodasLasCitas() {
        return ResponseEntity.ok(citaRepository.findAll());
    }

    @GetMapping("/facturas/todas")
    public ResponseEntity<List<Factura>> listarTodasLasFacturas() {
        return ResponseEntity.ok(facturaRepository.findAll());
    }

    @GetMapping("/bloqueos/todas")
    public ResponseEntity<List<BloqueoAgenda>> listarTodosLosBloqueos() {
        return ResponseEntity.ok(bloqueoAgendaRepository.findAll());
    }

    @DeleteMapping("/bloqueos/{id}")
    public ResponseEntity<?> eliminarBloqueo(@PathVariable Integer id) {
        try {
            bloqueoAgendaRepository.deleteById(id);
            return ResponseEntity.ok("{\"message\": \"Bloqueo eliminado correctamente.\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/mascotas/vacunas/{filename:.+}")
    public ResponseEntity<Resource> descargarCarnetVacunasRecepcion(@PathVariable String filename) {
        try {
            Path folderPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path rutaArchivo = folderPath.resolve(filename).normalize();
            Resource recurso = new UrlResource(rutaArchivo.toUri());

            if (recurso.exists() && recurso.isReadable()) {
                String tipoContenido = Files.probeContentType(rutaArchivo);
                if (tipoContenido == null) { tipoContenido = "application/octet-stream"; }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(tipoContenido))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                        .body(recurso);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/citas/estado/{idCita}")
    public ResponseEntity<?> cambiarEstadoCita(@PathVariable Integer idCita, @RequestParam String estado) {
        try {
            Cita cita = citaRepository.findById(idCita)
                    .orElseThrow(() -> new RuntimeException("Cita no localizada."));
            
            if ("CONFIRMADA".equalsIgnoreCase(estado)) {
                List<BloqueoAgenda> bloqueos = bloqueoAgendaRepository.findAll();
                for (BloqueoAgenda b : bloqueos) {
                    if (b.getFecha().equals(cita.getFecha())) {
                        if (b.getIdGroomer() == null || b.getIdGroomer().equals(cita.getIdGroomer())) {
                            if (b.getHoraInicio() == null || b.getHoraFin() == null) {
                                return ResponseEntity.badRequest().body("{\"error\": \"Imposible confirmar. El día completo se encuentra bloqueado por: " + b.getMotivo() + "\"}");
                            }
                            if (cita.getHoraInicio().isBefore(b.getHoraFin()) && cita.getHoraFinEstimada().isAfter(b.getHoraInicio())) {
                                return ResponseEntity.badRequest().body("{\"error\": \"Imposible confirmar. El horario intersecta con un bloqueo por: " + b.getMotivo() + "\"}");
                            }
                        }
                    }
                }

                Groomer groomer = cita.getGroomer();
                if (groomer == null && cita.getIdGroomer() != null) {
                    groomer = groomerRepository.findById(cita.getIdGroomer()).orElse(null);
                }

                if (groomer != null) {
                    final Groomer finalGroomer = groomer;
                    long solapadas = citaRepository.findAll().stream()
                            .filter(c -> c.getIdGroomer() != null && c.getIdGroomer().equals(finalGroomer.getId_groomer()))
                            .filter(c -> c.getFecha().equals(cita.getFecha()))
                            .filter(c -> !"CANCELADA".equalsIgnoreCase(c.getEstado()) && !"FINALIZADA".equalsIgnoreCase(c.getEstado()))
                            .filter(c -> !c.getId_cita().equals(idCita))
                            .filter(c -> cita.getHoraInicio().isBefore(c.getHoraFinEstimada()) && cita.getHoraFinEstimada().isAfter(c.getHoraInicio()))
                            .count();

                    int capacidadMaxima = (groomer.getCapacidad_simultanea() != null && groomer.getCapacidad_simultanea() > 0) 
                            ? groomer.getCapacidad_simultanea() : 5;

                    if (solapadas >= capacidadMaxima) {
                        return ResponseEntity.badRequest().body("{\"error\": \"Imposible aprobar solicitud. El groomer " + groomer.getNombres() + " ya cuenta con " + solapadas + " mascotas en paralelo en este horario.\"}");
                    }

                    LocalTime entrada = groomer.getHora_entrada();
                    LocalTime salida = groomer.getHora_salida();
                    if (entrada == null) entrada = LocalTime.of(8, 0);
                    if (salida == null) salida = LocalTime.of(18, 0);

                    if (cita.getHoraInicio().isBefore(entrada) || cita.getHoraFinEstimada().isAfter(salida)) {
                        String msgError = "El groomer " + groomer.getNombres() + " " + groomer.getApellidos() + 
                                          " no está disponible en ese horario. Su jornada es de " + 
                                          entrada.toString().substring(0,5) + " a " + salida.toString().substring(0,5);
                        return ResponseEntity.badRequest().body("{\"error\": \"" + msgError + "\"}");
                    }
                }
            }

            cita.setEstado(estado.toUpperCase());
            Cita actualizada = citaRepository.save(cita);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/facturas/registrar")
    public ResponseEntity<?> registrarFactura(@RequestBody Factura factura) {
        try {
            factura.setFecha_emision(LocalDate.now());
            Cita cita = citaRepository.findById(factura.getIdCita())
                    .orElseThrow(() -> new RuntimeException("La cita de referencia no existe."));
            Mascota mascota = mascotaRepository.findById(cita.getIdMascota())
                    .orElseThrow(() -> new RuntimeException("La mascota vinculada no existe."));
            factura.setIdCliente(mascota.getIdCliente());

            String correlativoUnico = "FAC-" + System.currentTimeMillis() / 1000;
            factura.setNumeroFactura(correlativoUnico);

            Double montoTotal = factura.getMontoTotal();
            factura.setTotal(montoTotal);
            factura.setSubtotal(Math.round((montoTotal * 0.87) * 100.0) / 100.0);
            factura.setImpuestos(Math.round((montoTotal * 0.13) * 100.0) / 100.0);

            Factura nuevaFactura = facturaRepository.save(factura);
            cita.setEstado("PAGADA");
            citaRepository.save(cita);
            
            return ResponseEntity.ok(nuevaFactura);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/bloqueos/registrar")
    public ResponseEntity<?> registrarBloqueo(@RequestBody BloqueoAgenda bloqueo) {
        try {
            if (bloqueo.getHoraInicio() != null) {
                bloqueo.setFechaInicio(LocalDateTime.of(bloqueo.getFecha(), bloqueo.getHoraInicio()));
            } else {
                bloqueo.setFechaInicio(LocalDateTime.of(bloqueo.getFecha(), LocalTime.MIN));
            }

            if (bloqueo.getHoraFin() != null) {
                bloqueo.setFechaFin(LocalDateTime.of(bloqueo.getFecha(), bloqueo.getHoraFin()));
            } else {
                bloqueo.setFechaFin(LocalDateTime.of(bloqueo.getFecha(), LocalTime.MAX));
            }

            BloqueoAgenda nuevoBloqueo = bloqueoAgendaRepository.save(bloqueo);
            return ResponseEntity.ok(nuevoBloqueo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}