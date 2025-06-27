package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class ArticuloDTO {

    private Long id;
    @NotBlank(message = "El código del artículo es obligatorio.")
    private String codArt;

    @NotBlank(message = "El nombre del artículo es obligatorio.")
    private String nomArt;

    @NotNull(message = "El precio de venta es obligatorio.")
    @Positive(message = "El precio de venta debe ser mayor a cero.")
    private Float precioVenta;

    private String descripcionArt; // puede ser nula o en blanco

    // No debe ser enviado por el cliente (se calcula o setea internamente)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHoraBajaArt;

    @PositiveOrZero(message = "El stock debe ser mayor a cero.")
    private int stock;

    // No debe ser enviado por el cliente
    private int stockSeguridad;

    @Positive(message = "La demanda diaria debe ser mayor a cero.")
    private int demandaDiaria;

    @Positive(message = "La desviación estándar en el uso debe ser mayor a cero.")
    private int desviacionEstandarUsoPeriodoEntrega;

    @Positive(message = "La desviación estándar durante el período de revisión debe ser mayor a cero.")
    private int desviacionEstandarDurantePeriodoRevisionEntrega;

    // Si vas a validar proveedor también, deberías usar @Valid acá
    private Long proveedorDTOID;
}

