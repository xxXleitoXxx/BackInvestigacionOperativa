package org.example.repository;
import org.example.entity.ProveedorArticulo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorArticuloRepository extends BaseRepository<ProveedorArticulo,Long>{

    List<ProveedorArticulo> findByFechaHoraBajaArtProvIsNull();

    //buscarInstanciaActivaProveedorArticuloSegunProveedorYArticulo
    @Query("SELECT pa FROM Proveedor p " + "JOIN p.proveedorArticulos pa " + "WHERE p.id = :idProveedor " + "AND pa.art.id = :idArticulo " + "AND pa.fechaHoraBajaArtProv IS NULL")
    Optional<ProveedorArticulo> buscarInstanciaActivaProveedorArticuloSegunProveedorYArticulo(@Param("idProveedor") Long idProveedor, @Param("idArticulo") Long idArticulo);



}
