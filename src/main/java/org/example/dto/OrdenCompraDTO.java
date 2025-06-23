package org.example.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class OrdenCompraDTO {

    private long id;
    private ArticuloDTO articuloDTO;
    private EstadoOrdenCompraDTO estadoOrdenCompraDTO;
    private int cantPedida;
    private  ProveedorDTO proveedorDTO;
    private String fecha;

}
