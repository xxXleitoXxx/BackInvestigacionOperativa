package org.example.repository;

import org.example.entity.Articulo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticuloRepository extends BaseRepository<Articulo,Long> {

    //findByCodArt
    Optional<Articulo> findByCodArt(String codArt);

    //findByFechaHoraBajaArtIsNull: busca todos los artículos activos no dados de baja.
    List<Articulo> findByFechaHoraBajaArtIsNull();

    //findByFechaHoraBajaArtIsNotNull: busca todos los artículos dados de baja
    List<Articulo> findByFechaHoraBajaArtIsNotNull();

}
