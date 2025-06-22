package org.example.repository;

import org.example.entity.Proveedor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends BaseRepository<Proveedor,Long>{

    //findByCodProv
    Optional <Proveedor> findByCodProv(String codProv);

    //findProveedoresActivosByArticuloId: Encontrar todos los proveedores por artículo.
    @Query("SELECT DISTINCT p FROM Proveedor p JOIN p.proveedorArticulos pa WHERE pa.art.id = :articuloId AND p.fechaHoraBajaProv IS NULL")
    List<Proveedor> findProveedoresActivosByArticuloId(@Param("articuloId") Long articuloId);

}
