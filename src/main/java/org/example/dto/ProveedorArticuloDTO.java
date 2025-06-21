package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.example.enums.TipoLote;
import java.util.Date;

@Data
public class ProveedorArticuloDTO {

    // Atributos
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fechaHoraBajaArtProv;
    private Float costoGeneralInventario;
    private int demoraEntrega;
    private int nivelDeServicio;
    private Float costoUnitario;
    private Float costoPedido;
    private Float costoMantenimiento;
    private int loteOptimo;
    private int puntoPedido;
    private int cantidadAPedir;
    private int inventarioMaximo;
    private int periodoRevision;
    public TipoLote tipoLote;

    // Relaciones
    private ArticuloDTO articuloDTO;

}

