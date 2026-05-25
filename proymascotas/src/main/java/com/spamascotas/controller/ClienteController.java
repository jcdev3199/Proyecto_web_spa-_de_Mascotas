package com.spamascotas.controller;

import com.spamascotas.dto.PerfilUpdateRequest;
import com.spamascotas.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.spamascotas.model.Cita;
import com.spamascotas.model.Cliente;
import com.spamascotas.model.Factura;
import com.spamascotas.model.FichaGrooming;
import com.spamascotas.model.Mascota;
import com.spamascotas.repository.ClienteRepository;
import com.spamascotas.repository.MascotaRepository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cliente")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ClienteController {

    @Autowired
    private AuthService authService;

    @Autowired 
    private MascotaRepository mascotaRepository;
    
    @Autowired 
    private ClienteRepository clienteRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final String uploadDir = "uploads/vacunas";

    @PutMapping("/perfil/{idUsuario}")
    public String editarPerfil(@PathVariable Integer idUsuario, 
                               @RequestBody PerfilUpdateRequest request, 
                               HttpServletRequest httpRequest) {
        if (idUsuario == null) {
            throw new RuntimeException("Error: El ID de usuario llegó nulo al servidor.");
        }
        return authService.actualizarPerfilCliente(idUsuario, request, httpRequest);
    }

    @GetMapping("/perfil/{idUsuario}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(authService.buscarDatosCliente(idUsuario));
    }

    @GetMapping("/mascotas/{idUsuario}")
    public ResponseEntity<List<Mascota>> listarMisMascotas(@PathVariable Integer idUsuario) {
        Cliente cliente = clienteRepository.findByUsuario_IdUsuario(idUsuario).orElse(null);
        if (cliente == null) {
            return ResponseEntity.ok(List.of()); 
        }
        return ResponseEntity.ok(mascotaRepository.findByIdCliente(cliente.getId_cliente()));
    }

    @PostMapping(value = "/mascotas/registrar/{idUsuario}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrarMascota(
            @PathVariable Integer idUsuario,
            @RequestParam("nombre") String nombre,
            @RequestParam("especie") String especie,
            @RequestParam(value = "raza", required = false) String raza,
            @RequestParam("tamano") String tamano,
            @RequestParam(value = "fechaNacimiento", required = false) String fechaNacimiento,
            @RequestParam(value = "alergias", required = false) String alergias,
            @RequestParam(value = "restricciones", required = false) String restricciones,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            Cliente cliente = clienteRepository.findByUsuario_IdUsuario(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Perfil de cliente no encontrado."));
            
            Mascota mascota = new Mascota();
            mascota.setIdCliente(cliente.getId_cliente());
            mascota.setNombre(nombre);
            mascota.setEspecie(especie);
            mascota.setRaza(raza);
            mascota.setTamano(tamano);
            
            if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
                mascota.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            }
            mascota.setAlergias(alergias);
            mascota.setRestricciones(restricciones);
            
            if (file != null && !file.isEmpty()) {
                File carpeta = new File(uploadDir);
                if (!carpeta.exists()) { carpeta.mkdirs(); }
                String nombreArchivo = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
                Path rutaCompleta = Paths.get(uploadDir).toAbsolutePath().resolve(nombreArchivo);
                Files.write(rutaCompleta, file.getBytes());
                mascota.setVacunas(nombreArchivo);
            } else {
                mascota.setVacunas("No adjuntado");
            }
            
            Mascota nueva = mascotaRepository.save(mascota);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(value = "/mascotas/actualizar/{idMascota}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizarMascota(
            @PathVariable Integer idMascota,
            @RequestParam("nombre") String nombre,
            @RequestParam("especie") String especie,
            @RequestParam(value = "raza", required = false) String raza,
            @RequestParam("tamano") String tamano,
            @RequestParam(value = "fechaNacimiento", required = false) String fechaNacimiento,
            @RequestParam(value = "alergias", required = false) String alergias,
            @RequestParam(value = "restricciones", required = false) String restricciones,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            Mascota mExistente = mascotaRepository.findById(idMascota)
                    .orElseThrow(() -> new RuntimeException("Mascota no localizada."));

            mExistente.setNombre(nombre);
            mExistente.setEspecie(especie);
            mExistente.setRaza(raza);
            mExistente.setTamano(tamano);
            
            if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
                mExistente.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            }
            mExistente.setAlergias(alergias);
            mExistente.setRestricciones(restricciones);

            if (file != null && !file.isEmpty()) {
                File carpeta = new File(uploadDir);
                if (!carpeta.exists()) { carpeta.mkdirs(); }
                if (mExistente.getVacunas() != null && !mExistente.getVacunas().equals("No adjuntado")) {
                    Path rutaAnterior = Paths.get(uploadDir).toAbsolutePath().resolve(mExistente.getVacunas());
                    Files.deleteIfExists(rutaAnterior);
                }
                String nombreArchivo = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
                Path rutaNueva = Paths.get(uploadDir).toAbsolutePath().resolve(nombreArchivo);
                Files.write(rutaNueva, file.getBytes());
                mExistente.setVacunas(nombreArchivo);
            }

            Mascota actualizada = mascotaRepository.save(mExistente);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/mascotas/vacunas/{filename:.+}")
    public ResponseEntity<Resource> verCarnetVacunas(@PathVariable String filename) {
        System.out.println("📥 [RECURSO DIRECTO] Procesando solicitud física inline para: " + filename);
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
                System.err.println("❌ [RECURSO DIRECTO] El archivo no existe o no es legible en disco: " + filename);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("❌ [RECURSO DIRECTO ERROR] Excepción al procesar archivo: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/mascotas/{idMascota}")
    @Transactional
    public ResponseEntity<?> eliminarMascota(@PathVariable Integer idMascota) {
        try {
            Mascota mascota = mascotaRepository.findById(idMascota)
                    .orElseThrow(() -> new RuntimeException("La mascota no existe."));

            if (mascota.getVacunas() != null && !mascota.getVacunas().equals("No adjuntado")) {
                Path rutaArchivo = Paths.get(uploadDir).toAbsolutePath().resolve(mascota.getVacunas());
                Files.deleteIfExists(rutaArchivo);
            }

            entityManager.createNativeQuery("DELETE FROM encuestas_satisfaccion WHERE id_cita IN (SELECT id_cita FROM citas WHERE id_mascota = :id)")
                    .setParameter("id", idMascota).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM facturas WHERE id_cita IN (SELECT id_cita FROM citas WHERE id_mascota = :id)")
                    .setParameter("id", idMascota).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM fichas_grooming WHERE id_cita IN (SELECT id_cita FROM citas WHERE id_mascota = :id)")
                    .setParameter("id", idMascota).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM citas WHERE id_mascota = :id")
                    .setParameter("id", idMascota).executeUpdate();

            mascotaRepository.delete(mascota);
            return ResponseEntity.ok("{\"message\": \"Mascota eliminada correctamente.\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    

    @GetMapping("/historial/citas/{idUsuario}")
    public ResponseEntity<?> obtenerHistorialCitasCliente(@PathVariable Integer idUsuario) {
        try {
            Cliente cliente = clienteRepository.findByUsuario_IdUsuario(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Perfil de cliente no localizado."));
            
            List<Cita> citas = entityManager.createQuery(
                "SELECT c FROM Cita c WHERE c.idMascota IN (SELECT m.id_mascota FROM Mascota m WHERE m.idCliente = :id)", Cita.class)
                .setParameter("id", cliente.getId_cliente())
                .getResultList();
                
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/historial/facturas/{idUsuario}")
    public ResponseEntity<?> obtenerHistorialFacturasCliente(@PathVariable Integer idUsuario) {
        try {
            Cliente cliente = clienteRepository.findByUsuario_IdUsuario(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Perfil de cliente no localizado."));
            
            List<Factura> facturas = entityManager.createQuery(
                "SELECT f FROM Factura f WHERE f.idCliente = :id", Factura.class)
                .setParameter("id", cliente.getId_cliente())
                .getResultList();
                
            return ResponseEntity.ok(facturas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/historial/grooming/{idUsuario}")
    public ResponseEntity<?> obtenerHistorialFichasGroomingCliente(@PathVariable Integer idUsuario) {
        try {
            Cliente cliente = clienteRepository.findByUsuario_IdUsuario(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Perfil de cliente no localizado."));
            
            List<FichaGrooming> fichas = entityManager.createQuery(
                "SELECT fg FROM FichaGrooming fg WHERE fg.idCita IN (SELECT c.id_cita FROM Cita c WHERE c.idMascota IN (SELECT m.id_mascota FROM Mascota m WHERE m.idCliente = :id))", FichaGrooming.class)
                .setParameter("id", cliente.getId_cliente())
                .getResultList();
                
            return ResponseEntity.ok(fichas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}