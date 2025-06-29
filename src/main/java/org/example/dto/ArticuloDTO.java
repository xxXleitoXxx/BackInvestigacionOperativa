package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class ArticuloDTO {

    private Long id;
    private String codArt;
    private String nomArt;
    private Float precioVenta;
    private String descripcionArt; // puede ser nula o en blanco

    // No debe ser enviado por el cliente (se calcula o setea internamente)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHoraBajaArt;
    private int stock;

    // No debe ser enviado por el cliente
    private int stockSeguridad;
    private int demandaDiaria;

    private int desviacionEstandar;

    // Si vas a validar proveedor también, deberías usar @Valid acá
    private ProveedorDTO proveedorDTO;
}

