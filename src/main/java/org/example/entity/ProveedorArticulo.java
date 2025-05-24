package org.example.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@Data
public class ProveedorArticulo extends BaseEntity{

    private int PrecioArtProv;
    private Date fechaHoraBajaArtProv;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Articulo art;
}
