package org.example.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Data
public class VentaDTO {

    private long id;
    private LocalDateTime fechaHoraVentDTO;
    private float montoTotalVentDTO;

    private List<VentaArticuloDTO> ventaArticuloDTOS = new ArrayList<>();

}
