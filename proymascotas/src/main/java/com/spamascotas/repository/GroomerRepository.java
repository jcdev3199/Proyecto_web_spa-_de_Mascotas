package com.spamascotas.repository;

import com.spamascotas.model.Groomer;
import com.spamascotas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GroomerRepository extends JpaRepository<Groomer, Integer> {
    Optional<Groomer> findByUsuario(Usuario usuario); 
}