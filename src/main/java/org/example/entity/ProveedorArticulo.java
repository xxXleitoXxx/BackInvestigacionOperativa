package org.example.entity;
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
    private Date fechaHoraBajaArtProv;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Articulo art;
}
