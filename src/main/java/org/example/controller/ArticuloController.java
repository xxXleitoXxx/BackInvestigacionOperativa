package org.example.controller;

import org.example.entity.Articulo;
import org.example.services.ArticuloServiceImp;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/articulo")
public class ArticuloController extends BaseControllerImpl<Articulo, ArticuloServiceImp> {

    private ArticuloServiceImp artServicio;

    public ArticuloController(){

    }

    @PostMapping({"/ambArt"})
    public int /*ResponseEntity<lo que devuelve la funcion>*/ ABMarticulo(@RequestBody String respuesta){

        return 0;
    }

}
