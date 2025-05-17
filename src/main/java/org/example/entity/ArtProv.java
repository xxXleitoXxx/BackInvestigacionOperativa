package org.example.entity;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Data
@Getter
@Setter
public class ArtProv {

    private int PrecioArtProv;
    private Date fechaHoraBajaArtProv;

}
