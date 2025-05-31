package org.example.entity;
import jakarta.persistence.*;

import jakarta.validation.constraints.Size;
import lombok.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Proveedor extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String codProv;
    private String nomProv;
    private String descripcionProv;
    private Date fechaHoraBajaProv;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    @JoinColumn(nullable = false, name = "ProvArt")
    @Size(min = 1)
    private List<ProveedorArticulo> proveedorArticulos = new ArrayList<>();



}
