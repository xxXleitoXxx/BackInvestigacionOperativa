package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrdenCompra extends BaseEntity{

    private LocalDateTime fechaLlegadaOrdCom;
    private LocalDateTime fechaPedidoOrdCom;
    private float montoTotalOrdCom;
    private int cantPedida;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private Proveedor prov;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private EstadoOrdenCompra estadoOrdCom;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private Articulo articulo;



}
