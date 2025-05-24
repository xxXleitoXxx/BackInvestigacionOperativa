package org.example.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data

public class VentaArticulo extends BaseEntity{

    private int cantArtVent;
    private int montoArt;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Articulo art;


}
