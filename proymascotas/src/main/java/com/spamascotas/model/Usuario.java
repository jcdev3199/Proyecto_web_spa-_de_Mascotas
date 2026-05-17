package com.spamascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_usuario;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String password_hash;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String estado = "Activo"; 

    @Column(name = "intentos_fallidos")
    private Integer intentos_fallidos = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueado_hasta;

    @Column(name = "two_fa_secret")
    private String twoFaSecret;

    public String getTwoFaSecret() { return twoFaSecret; }
    public void setTwoFaSecret(String twoFaSecret) { this.twoFaSecret = twoFaSecret; }

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fecha_creacion;
}