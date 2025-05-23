package org.example.DTO;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data

public class ArtDTO {

    private int id;
    private String nomArt;
    private int stock;
    private String descriocionArt;

}
