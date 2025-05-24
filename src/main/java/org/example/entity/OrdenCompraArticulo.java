package org.example.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)

public class OrdenCompraArticulo extends BaseEntity{

    private int cantArtPedida;


    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Articulo art;


}
