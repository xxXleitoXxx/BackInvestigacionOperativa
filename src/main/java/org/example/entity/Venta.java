package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
public class Venta extends BaseEntity{

    private LocalDateTime fechaHoraVent;
    private float montoTotalVent;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_Venta")
    private List<VentaArticulo> ventaArticulos = new ArrayList<>();



}
