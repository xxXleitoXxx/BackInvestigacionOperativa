package org.example.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Data

public class EstadoOrdenCompra extends BaseEntity{

    private String codEOC;
    private String nomEOC;
    private String descripcionEOC;
    private Date fechaHoraBajaECO;

}
