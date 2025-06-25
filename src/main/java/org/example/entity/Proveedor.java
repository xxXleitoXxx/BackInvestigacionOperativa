package org.example.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // Importar NotBlank
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Proveedor extends BaseEntity {

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El código del proveedor es obligatorio.") // Añadido
    private String codProv;
    @NotBlank(message = "El nombre del proveedor es obligatorio.") // Añadido
    private String nomProv;
    private String descripcionProv;
    private Date fechaHoraBajaProv;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    @JoinColumn(nullable = false, name = "ProvArt")
    @Size(min = 1)
    private List<ProveedorArticulo> proveedorArticulos = new ArrayList<>();

}
//@Size(min = 1)