package org.example.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Data
public class ArticuloDTO {

    // Atributos
    private Long id;
    private String codArt;
    private String nomArt;
    private Float precioVenta;
    private String descripcionArt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHoraBajaArt;
    private int stock;
    private int stockSeguridad;
    private int demandaDiaria;
    private int desviacionEstandarUsoPeriodoEntrega;
    private int desviacionEstandarDurantePeriodoRevisionEntrega;

    // Relaciones
    private Long proveedorElegidoId;
    private String proveedorElegidoNomProv;
}
