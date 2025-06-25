package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArticuloOCDTO {

    private Long id;
    @NotBlank(message = "El nombre del artículo es obligatorio.")
    private String nomArt;
}
