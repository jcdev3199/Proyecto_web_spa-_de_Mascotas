package com.spamascotas.repository;

import com.spamascotas.model.DetalleChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleChecklistRepository extends JpaRepository<DetalleChecklist, Integer> {
}