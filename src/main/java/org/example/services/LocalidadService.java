package org.example.services;

import org.example.entity.Localidad;
import org.example.repository.LocalidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LocalidadService {
    @Autowired
    private LocalidadRepository localidadRepository;

    public Localidad guardarLocalidad(Localidad localidad){
        return localidadRepository.save(localidad);

    }
//    public List<Localidad> guardarLocalidades(List<Localidad> localidades) {
//        localidades.forEach(localidad -> localidadRepository.save(localidad));
//        return localidades;
//    }
    public List<Localidad> Obtener(){
        return localidadRepository.findAll();
    }

}
