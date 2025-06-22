package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank; // Importar NotBlank
import lombok.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Data
public class ProveedorDTO {

    // Atributos
    private Long id;
    @NotBlank(message = "El código del proveedor es obligatorio.") // Añadido
    private String codProv;
    @NotBlank(message = "El nombre del proveedor es obligatorio.") // Añadido
    private String nomProv;
    private String descripcionProv;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fechaHoraBajaProv;

    // Relaciones
    private List<ProveedorArticuloDTO> proveedorArticulos = new ArrayList<>();
}