package org.example.repository;

import org.example.entity.Articulo;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticuloRepository extends BaseRepository<Articulo,Long> {
}
