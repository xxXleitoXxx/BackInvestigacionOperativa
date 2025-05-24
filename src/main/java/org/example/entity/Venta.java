package org.example.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
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

public class Venta extends BaseEntity{

    private String codVent;
    private Date fechaHoraVent;
    private int montoTotalVent;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VentaArticulo> ventaArticulos = new ArrayList<>();



}
