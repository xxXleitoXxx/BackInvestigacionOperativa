package org.example.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
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
    private List<VentaArticulo> ventaArticulos = new ArrayList<>();



}
