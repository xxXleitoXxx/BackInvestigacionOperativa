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
    private Float costoGeneralInventario; //Se calcula
    private int demoraEntrega; // Se carga
    private int nivelDeServicio;//Se carga
    private Float costoUnitario; //Se carga
    private  Float costoPedido; // se carga
    private Float costoMantenimiento; //se carga
    private  int loteOptimo; //eoq se calcula
    //Inventario Lote Fijo
    private int puntoPedido; //R punto de volver a pedir.Este es el punto de volver a pedir. Se calcula.
    //Inventario Periodo Fijo
    private  int cantidadAPedir; //q se calcula
    private int inventarioMaximo; //stock seguridad + q se calcula
    private int periodoRevision;//Se carga


    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JsonProperty("articulo")
    private Articulo art; //acá tendría que haber una etiqueta para que no sea nulo

    //Tipo de lote
    public TipoLote tipoLote;
}
