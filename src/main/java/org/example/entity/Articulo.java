package org.example.entity;
import jakarta.persistence.Entity;
import lombok.*;
import org.example.TipoLote;

import java.util.Date;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)

public class Articulo extends BaseEntity{

    private String codArt;
    private String nomArt;
    private int stock;
    private int PrecioVenta;
    private String descriocionArt;
    private Date fechaHoraBajaArt;
    private Proveedor provedorElegido;
    public TipoLote tipoLote;
    private int sotckSeguridad;



}
