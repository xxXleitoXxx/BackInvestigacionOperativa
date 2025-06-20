package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)

public class Venta extends BaseEntity{

    private String codVent;
    private Date fechaHoraVent;
    private int montoTotalVent;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_Venta")
    private List<VentaArticulo> ventaArticulos = new ArrayList<>();



}
