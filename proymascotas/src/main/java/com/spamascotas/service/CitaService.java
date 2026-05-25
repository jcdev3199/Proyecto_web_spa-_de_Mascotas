package com.spamascotas.service;

import com.spamascotas.dto.CitaRequest;
import com.spamascotas.model.*;
import com.spamascotas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class CitaService {

    @Autowired private CitaRepository citaRepository;
    @Autowired private GroomerRepository groomerRepository;
    @Autowired private ServicioRepository servicioRepository;
    @Autowired private BloqueoAgendaRepository bloqueoAgendaRepository;
    @Autowired private MascotaRepository mascotaRepository;

    @Transactional
    public Cita solicitarCita(CitaRequest request) {
        System.out.println("⚙️ [DEBUG ALGORITMO] Iniciando cálculo de slots y restricciones...");

        Groomer groomer = groomerRepository.findById(request.getIdGroomer())
                .orElseThrow(() -> new RuntimeException("Groomer no encontrado"));
                
        Servicio servicio = servicioRepository.findById(request.getIdServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        Mascota mascota = mascotaRepository.findById(request.getIdMascota())
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        int duracionBase = servicio.getDuracionBaseMinutos();
        double multiplicador = 1.0;
        
        String tamanoMascota = (mascota.getTamano() != null) ? mascota.getTamano().toLowerCase().trim() : "pequeño";

        switch (tamanoMascota) {
            case "mediano":
                multiplicador = 1.10;
                break;
            case "grande":
                multiplicador = 1.15;
                break;
            case "gigante":
                multiplicador = 1.30;
                break;
            default:
                multiplicador = 1.00;
                break;
        }

        long duracionFinalMinutos = Math.round(duracionBase * multiplicador);
        LocalTime horaFinEstimada = request.getHoraInicio().plusMinutes(duracionFinalMinutos);

        System.out.println("📊 [ALGORITMO] Mascota: " + mascota.getNombre() + " (" + tamanoMascota + ") | Base: " + duracionBase + "m | Final: " + duracionFinalMinutos + "m");

        List<BloqueoAgenda> bloqueosActivos = bloqueoAgendaRepository.findAll();
        for (BloqueoAgenda b : bloqueosActivos) {
            if (b.getFecha().equals(request.getFecha())) {
                if (b.getIdGroomer() == null || b.getIdGroomer().equals(request.getIdGroomer())) {
                    if (b.getHoraInicio() == null || b.getHoraFin() == null) {
                        throw new RuntimeException("La fecha seleccionada se encuentra bloqueada por: " + b.getMotivo());
                    }
                    if (request.getHoraInicio().isBefore(b.getHoraFin()) && horaFinEstimada.isAfter(b.getHoraInicio())) {
                        throw new RuntimeException("El estilista no está disponible en este horario por motivo de: " + b.getMotivo());
                    }
                }
            }
        }

        List<Cita> citasActivasDelDia = citaRepository.findCitasActivasDelDia(groomer, request.getFecha());
        
        long solapadas = citasActivasDelDia.stream()
                .filter(c -> !"CANCELADA".equalsIgnoreCase(c.getEstado()) && !"FINALIZADA".equalsIgnoreCase(c.getEstado()))
                .filter(c -> request.getHoraInicio().isBefore(c.getHoraFinEstimada()) && horaFinEstimada.isAfter(c.getHoraInicio()))
                .count();

        int capacidadMaximaGroomer = (groomer.getCapacidad_simultanea() != null && groomer.getCapacidad_simultanea() > 0) 
                ? groomer.getCapacidad_simultanea() : 5;

        System.out.println("✂️ [CAPACIDAD] Solapadas detectadas: " + solapadas + " / Máxima permitida: " + capacidadMaximaGroomer);

        if (solapadas >= capacidadMaximaGroomer) {
            throw new RuntimeException("Capacidad máxima superada. El estilista elegido ya tiene " + solapadas + " mascotas en su mesa para este bloque horario.");
        }

        Cita cita = new Cita();
        cita.setIdMascota(request.getIdMascota());
        cita.setGroomer(groomer);
        cita.setServicio(servicio);
        cita.setFecha(request.getFecha());
        cita.setHoraInicio(request.getHoraInicio());
        cita.setHoraFinEstimada(horaFinEstimada); 
        cita.setEstado("PENDIENTE");

        return citaRepository.save(cita);
    }
    
    public List<Groomer> listarGroomers() { return groomerRepository.findAll(); }
    public List<Servicio> listarServicios() { return servicioRepository.findAll(); }
}