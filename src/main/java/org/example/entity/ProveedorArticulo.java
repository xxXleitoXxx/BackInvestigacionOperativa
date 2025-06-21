package org.example.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.example.enums.TipoLote;

import java.util.Date;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)

public class ProveedorArticulo extends BaseEntity{

    private Date fechaHoraBajaArtProv;
    //Atributos Inventario
    private Float costoGeneralInventario;
    private int demoraEntrega;
    private Float costoUnitario;
    private  Float costoPedido;
    private Float costoMantenimiento;
    private  int cantidadOptima; //eoq
    //Inventario Lote Fijo
    private int loteOptimo; //R punto de volver a pedir
    //Inventario Periodo Fijo
    private int inventarioMaximo; //q cantidad a pedir.
    private int periodoRevision;
    private int nivelDeServicio;//Se carga

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JsonProperty("articulo")
    private Articulo art; //acá tendría que haber una etiqueta para que no sea nulo

    //Tipo de lote
    public TipoLote tipoLote;
}
