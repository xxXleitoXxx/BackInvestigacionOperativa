package org.example.entity;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class EstadoOrdenCompra extends BaseEntity{

    private String codEOC;
    private String nomEOC;
    private String descripcionEOC;
    private Date fechaHoraBajaECO;

}
