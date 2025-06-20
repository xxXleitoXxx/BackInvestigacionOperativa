package org.example.repository;

import org.example.entity.Articulo;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticuloRepository extends BaseRepository<Articulo,Long> {

    //findByCodArt
    public Optional<Articulo> findByCodArt(String codArt);


}
