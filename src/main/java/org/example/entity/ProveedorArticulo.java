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

    private int precioArtProv;
    private Date fechaHoraBajaArtProv;
    private int demoraEntrega;
    private  Float costoPedido;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JsonProperty("articulo")
    private Articulo art; //acá tendría que haber una etiqueta para que no sea nulo

    //Tipo de lote
    public TipoLote tipoLote;
}
