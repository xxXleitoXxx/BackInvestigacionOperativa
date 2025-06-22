package org.example.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)

public class Articulo extends BaseEntity{

    //Atributos
    @NotBlank(message = "El código del artículo es obligatorio.")
    private String codArt;
    @NotBlank(message = "El nombre del artículo es obligatorio.")
    private String nomArt;
    @NotNull(message = "El precio de venta es obligatorio.")
    @Positive(message = "El precio de venta debe ser mayor a cero.")
    private Float precioVenta;
    private String descripcionArt;
    private LocalDateTime fechaHoraBajaArt;

    //Atributos para cálculo de inventario
    @PositiveOrZero(message = "El stock debe ser mayor o igual a cero.") // Modificado para permitir 0
    private int stock; //se carga
    @PositiveOrZero(message = "El stock debe ser mayor o igual a cero.")
    private int stockSeguridad; //se calcula
    @Positive(message = "La demanda diaria debe ser mayor a cero.")
    private int demandaDiaria; //se carga
    @Positive(message = "La desviación estándar en el uso debe ser mayor a cero.")
    private int desviacionEstandarUsoPeriodoEntrega; //se carga
    @Positive(message = "La desviación estándar durante el período de revisión debe ser mayor a cero.")
    private int desviacionEstandarDurantePeriodoRevisionEntrega;

    //Relaciones
    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "Proveedor_id")
    private Proveedor proveedorElegido;


}
