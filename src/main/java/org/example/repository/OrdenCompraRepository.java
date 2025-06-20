package org.example.repository;

import org.example.entity.OrdenCompra;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraRepository extends BaseRepository<OrdenCompra,Long>{

    //findByEstado
    @Query("SELECT oc FROM OrdenCompra oc WHERE oc.estadoOrdCom.codEOC = :codigo")
    List<OrdenCompra> findByEstadoCodigo(@Param("codigo") String codigo);


}
