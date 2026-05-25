package com.spamascotas.controller;

import com.spamascotas.model.*;
import com.spamascotas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/groomer")
@CrossOrigin(origins = "http://localhost:5173")
public class GroomerController {

    @Autowired private CitaRepository citaRepository;
    @Autowired private GroomerRepository groomerRepository;
    @Autowired private FichaGroomingRepository fGroomingRepository;
    @Autowired private ChecklistItemRepository checklistItemRepository;
    @Autowired private DetalleChecklistRepository detalleChecklistRepository;

    @GetMapping("/citas/{idUsuario}")
    public ResponseEntity<?> listarMiAgendaGrooming(@PathVariable Integer idUsuario) {
        Groomer groomer = groomerRepository.findAll().stream()
                .filter(g -> g.getUsuario() != null && g.getUsuario().getId_usuario().equals(idUsuario))
                .findFirst().orElse(null);

        if (groomer == null) {
            return ResponseEntity.ok(List.of());
        }
        
        List<Cita> miAgenda = citaRepository.findAll().stream()
                .filter(c -> c.getIdGroomer() != null && c.getIdGroomer().equals(groomer.getId_groomer()))
                .filter(c -> "CONFIRMADA".equalsIgnoreCase(c.getEstado()) || "PAGADA".equalsIgnoreCase(c.getEstado()) || "FINALIZADA".equalsIgnoreCase(c.getEstado()))
                .toList();

        return ResponseEntity.ok(miAgenda);
    }

    @GetMapping("/checklist/items")
    public ResponseEntity<List<ChecklistItem>> obtenerItemsChecklist() {
        List<ChecklistItem> items = checklistItemRepository.findAll();
        if (items.isEmpty()) {
            String[] tareasDefecto = {"Corte de Uñas", "Limpieza de Oídos", "Drenado de Glándulas Anales", "Corte Estilístico", "Baño e Hidratación", "Perfume Institucional"};
            for (String tarea : tareasDefecto) {
                ChecklistItem c = new ChecklistItem();
                c.setDescripcionTarea(tarea);
                checklistItemRepository.save(c);
            }
            items = checklistItemRepository.findAll();
        }
        return ResponseEntity.ok(items);
    }

    @PostMapping("/ficha/guardar")
    public ResponseEntity<?> registrarInformeGrooming(@RequestBody FichaGrooming fue, @RequestParam List<Integer> itemsCompletados) {
        try {
            Optional<FichaGrooming> existenteOpt = fGroomingRepository.findByIdCita(fue.getIdCita());
            if (existenteOpt.isPresent()) {
                fue.setId_ficha(existenteOpt.get().getId_ficha());
                System.out.println("🔄 [UPSERT] Actualizando ficha técnica existente ID: #" + existenteOpt.get().getId_ficha() + " para la cita: " + fue.getIdCita());
            } else {
                System.out.println("📥 [UPSERT] Creando nueva ficha técnica limpia para la cita: " + fue.getIdCita());
            }

            FichaGrooming guardada = fGroomingRepository.save(fue);

            Cita cita = citaRepository.findById(fue.getIdCita()).orElse(null);
            if (cita != null && !"PAGADA".equalsIgnoreCase(cita.getEstado())) {
                cita.setEstado("FINALIZADA");
                citaRepository.save(cita);
            }

            for (Integer itemId : itemsCompletados) {
                DetalleChecklist d = new DetalleChecklist();
                d.setIdFicha(guardada.getId_ficha());
                d.setIdItem(itemId);
                d.setCompletado((byte) 1);
                d.setObservacionEspecifica("Procesado correctamente por el estilista.");
                detalleChecklistRepository.save(d);
            }

            return ResponseEntity.ok(guardada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}