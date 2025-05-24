package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Data

public class OrdenCompra extends BaseEntity{

    private String codOrdCom;
    private Date fechaLlegadaOrdCom;
    private Date fechaPedidoOrdCom;
    private int montoTotalOrdCom;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Proveedor prov;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private EstadoOrdenCompra estadoOrdCom;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrdenCompraArticulo> OrdenCompraArticulo = new ArrayList<>();



}
