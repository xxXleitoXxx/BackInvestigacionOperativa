package org.example.entity;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Data
@Getter
@Setter
public class Articulo {

    private String codArt;
    private String nomArt;
    private int stock;
    private String descriocionArt;
    private Date fechaHoraBajaArt;


}
