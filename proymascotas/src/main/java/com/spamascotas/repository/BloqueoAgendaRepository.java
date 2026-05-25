package com.spamascotas.repository;

import com.spamascotas.model.BloqueoAgenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BloqueoAgendaRepository extends JpaRepository<BloqueoAgenda, Integer> {
}