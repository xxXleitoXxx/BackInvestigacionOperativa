package org.example.controller;

import netscape.javascript.JSObject;
import org.example.DTO.ArtDTO;
import org.example.entity.Articulo;
import org.example.services.ArticuloServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/articulo")
public class ArticuloController extends BaseControllerImpl<Articulo, ArticuloServiceImp> {

    private ArticuloServiceImp artServicio;

    public ArticuloController(){

    }

    @PostMapping({"/ambArt"})
    public int /*ResponseEntity<lo que devuelve la funcion>*/ ABMarticulo(@RequestBody String respuesta, @RequestBody ArtDTO dto){


        switch(respuesta){
            case "alta": artServicio.alta(dto); break;
            case "baja": artServicio.baja(dto); break;
            //case "mod" : artServicio.mod(dto); break;
        }

        return 0;
    }

}
