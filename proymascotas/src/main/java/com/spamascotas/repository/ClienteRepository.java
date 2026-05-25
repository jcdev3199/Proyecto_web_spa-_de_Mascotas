package com.spamascotas.repository;

import com.spamascotas.model.Cliente;
import com.spamascotas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByUsuario(Usuario usuario);

    @Query(value = "SELECT * FROM clientes WHERE id_usuario = :idUsuario", nativeQuery = true)
    Optional<Cliente> findByUsuario_IdUsuario(@Param("idUsuario") Integer idUsuario);
}