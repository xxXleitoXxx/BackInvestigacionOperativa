package org.example.controller;

import org.example.entity.Articulo;
import org.example.services.clasesImp.ArticuloServiceImp;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/articulo")
public class ArticuloController extends BaseControllerImpl<Articulo, ArticuloServiceImp> {

    private ArticuloServiceImp artServicio;

    public ArticuloController(){

    }


}
