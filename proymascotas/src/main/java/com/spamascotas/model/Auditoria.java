package com.spamascotas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "auditoria")
@Data
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLog;

    private Long idUsuario; 
    private String email;
    private String rol;     
    private LocalDateTime fechaHora; 
    private String ip;      
    
    @Column(columnDefinition = "TEXT")
    private String navegador; 
    
    private String accion;
}