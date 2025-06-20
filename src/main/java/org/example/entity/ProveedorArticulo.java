package org.example.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)

public class ProveedorArticulo extends BaseEntity{

    private int PrecioArtProv;
    private boolean tipoModeloInventario;
    private Date fechaHoraBajaArtProv;
    private int demoraEntrega;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JsonProperty("articulo")
    private Articulo art;
}
