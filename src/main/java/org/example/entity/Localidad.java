package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Entity
@Data
@Getter
@Setter
public class Localidad {
    @Id
    private int codLocalidad;
    private String nombreLocalidad;
    private Date fechaBajaLocalidad;

    // Getters and Setters
}
