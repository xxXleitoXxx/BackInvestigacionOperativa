package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProveedorOCDTO {
    private Long id;
    @NotBlank(message = "El nombre del proveedor es obligatorio.") // Añadido
    private String nomProv;
}
