package com.spamascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Data
class DetalleChecklistId implements Serializable {
    private Integer idFicha;
    private Integer idItem;
}

@Entity
@Table(name = "detalle_checklist")
@IdClass(DetalleChecklistId.class)
@Data
public class DetalleChecklist {

    @Id
    @Column(name = "id_ficha")
    private Integer idFicha;

    @Id
    @Column(name = "id_item")
    private Integer idItem;

    private Byte completado;

    @Column(name = "observacion_especifica", length = 255)
    private String observacionEspecifica;
}