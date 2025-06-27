package org.example.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=false)
public class OrdenCompraDTO {

    private long id;
    private ArticuloOCDTO articuloDTO;
    private EstadoOrdenCompraDTO estadoOrdenCompraDTO;
    private int cantPedida;
    private ProveedorOCDTO proveedorDTO;
    private String fecha;
    private float montoTotal;
    private LocalDateTime fechaPedidoOrdCom;
    private LocalDateTime fechaLlegadaOrdCom;

}
