package com.spamascotas.repository;

import com.spamascotas.model.FichaGrooming;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FichaGroomingRepository extends JpaRepository<FichaGrooming, Integer> {
    Optional<FichaGrooming> findByIdCita(Integer idCita);
}