package org.example.repository;

import org.example.entity.Localidad;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalidadRepository extends JpaRepository<Localidad,Long> {


}
