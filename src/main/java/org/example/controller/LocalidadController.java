package org.example.controller;

import org.example.entity.Localidad;
import org.example.services.LocalidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/localidades")
public class LocalidadController {
@Autowired
private LocalidadService localidadService;
    @PostMapping
public Localidad guardarLocalidad(@RequestBody Localidad localidad){
        return localidadService.guardarLocalidad(localidad);
    }
//    @PostMapping
//    public Localidad guardarLocalidad(@RequestBody List<Localidad> localidad){
//        return localidadService.guardarLocalidad(localidad);
//    }
    @GetMapping
    public List<Localidad> ObtenerLocalidad(){
        return localidadService.Obtener();
    }

}
