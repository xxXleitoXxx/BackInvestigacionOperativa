package org.example.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Data

public class Articulo extends BaseEntity{

    private String codArt;
    private String nomArt;
    private int stock;
    private String descriocionArt;
    private Date fechaHoraBajaArt;
    private Provedor provedorElegido;



}
