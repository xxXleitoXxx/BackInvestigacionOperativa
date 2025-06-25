package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EstadoOrdenCompra extends BaseEntity{

    private String codEOC;
    private String nomEOC;
    private String descripcionEOC;
    private Date fechaHoraBajaECO;

}
