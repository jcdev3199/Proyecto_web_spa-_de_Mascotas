package com.spamascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
class InsumosServicioId implements Serializable {
    private Integer idServicio;
    private Integer idProducto;
}

@Entity
@Table(name = "insumos_servicio")
@IdClass(InsumosServicioId.class)
@Data
public class InsumosServicio {

    @Id
    @Column(name = "id_servicio")
    private Integer idServicio;

    @Id
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "cantidad_estimada")
    private BigDecimal cantidadEstimada;
}