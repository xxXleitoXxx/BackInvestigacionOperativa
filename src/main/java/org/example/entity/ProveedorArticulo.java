package org.example.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProveedorArticulo extends BaseEntity{

    private Date fechaHoraBajaArtProv;
    //Atributos Inventario
    private Float costoGeneralInventario; //Se calcula
    @PositiveOrZero(message = "la demora de entrega debe ser mayor a cero.")
    private int demoraEntrega; // Se carga
    private int nivelDeServicio;//Se carga
    @PositiveOrZero(message = "El costo unitario debe ser mayor a cero.")
    private Float costoUnitario; //Se carga
    @PositiveOrZero(message = "El costo de pedido debe ser mayor a cero.")
    private  Float costoPedido; // se carga
    //este atributo parte de articulo
    @PositiveOrZero(message = "El costo de mantenimiento debe ser mayor a cero.")
    private Float costoMantenimiento; //se carga
    //---------------------------
    private  int loteOptimo; //eoq se calcula
    //Inventario Lote Fijo
    private int puntoPedido; //R punto de volver a pedir.Este es el punto de volver a pedir. Se calcula.
    //Inventario Periodo Fijo
    private  int cantidadAPedir; //q se calcula
    private int inventarioMaximo; //stock seguridad + q se calcula
    @PositiveOrZero(message = "El precio de venta debe ser mayor a cero.")
    private int periodoRevision;//Se carga


    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JsonProperty("articulo")
    private Articulo art; //acá tendría que haber una etiqueta para que no sea nulo

    //Tipo de lote
    public TipoLote tipoLote;
}
