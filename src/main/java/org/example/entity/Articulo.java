package org.example.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private String codArt;
    private String nomArt;
    private float PrecioVenta;
    private String descripcionArt;
    private LocalDateTime fechaHoraBajaArt;
    //

    //Atributos para cálculo de inventario
    private int stock;
    private int stockSeguridad;
    private float costoGeneralInventario;
    //Lote fijo
    private int loteOptimo;
    private int puntoPedido;
    //Periodo fijo
    private int inventarioMaximo;

    //Relaciones
    @ManyToOne
    @JoinColumn(name = "Proveedor_id")
    private Proveedor provedorElegido;


}
