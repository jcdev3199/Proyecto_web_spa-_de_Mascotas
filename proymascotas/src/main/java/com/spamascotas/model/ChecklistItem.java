package com.spamascotas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "checklist_items")
@Data
public class ChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_item;

    @Column(name = "descripcion_tarea", nullable = false, length = 255)
    private String descripcionTarea;
}