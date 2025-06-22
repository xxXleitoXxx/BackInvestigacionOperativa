package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@EqualsAndHashCode(callSuper=false)

public class OrdenCompra extends BaseEntity{

    private String codOrdCom;
    private Date fechaLlegadaOrdCom;
    private Date fechaPedidoOrdCom;
    private float montoTotalOrdCom;
    private int cantPedida;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private Proveedor prov;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private EstadoOrdenCompra estadoOrdCom;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private Articulo articulo;



}
