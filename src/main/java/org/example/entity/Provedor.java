package org.example.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@Getter
@Setter
public class Provedor extends BaseEntity {

    private String codProv;
    private String nomProv;
    private String descripcionProv;
    private Date fechaHoraBajaProv;

}
