package com.spamascotas.repository;

import com.spamascotas.model.Cita;
import com.spamascotas.model.Groomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    @Query("SELECT c FROM Cita c WHERE c.groomer = :groomer AND c.fecha = :fecha AND c.estado != 'CANCELADA'")
    List<Cita> findCitasActivasDelDia(@Param("groomer") Groomer groomer, @Param("fecha") LocalDate fecha);
}