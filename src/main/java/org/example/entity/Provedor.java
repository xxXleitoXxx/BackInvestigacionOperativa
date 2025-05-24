package org.example.entity;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ArtProv> ArtProv = new ArrayList<>();



}
