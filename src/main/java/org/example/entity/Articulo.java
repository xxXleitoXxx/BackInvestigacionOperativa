package org.example.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    private Float precioVenta;
    private String descripcionArt;
    private LocalDateTime fechaHoraBajaArt;

    //Atributos para cálculo de inventario
    private int stock; //se carga
    private int stockSeguridad; //se calcula
    private int demandaDiaria; //se carga
    private int desviacionEstandarUsoPeriodoEntrega; //se carga
    private int desviacionEstandarDurantePeriodoRevisionEntrega;




    //Relaciones
    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "Proveedor_id")
    private Proveedor proveedorElegido;


}
