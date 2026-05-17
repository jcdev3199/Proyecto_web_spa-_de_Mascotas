package com.spamascotas.repository;

import com.spamascotas.model.TokenActivacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenActivacionRepository extends JpaRepository<TokenActivacion, Long> {
    Optional<TokenActivacion> findByToken(String token);
    void deleteByUsuario(com.spamascotas.model.Usuario usuario);
}