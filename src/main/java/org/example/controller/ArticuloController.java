package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.entity.Articulo;
import org.example.services.clasesImp.ArticuloServiceImp;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/Articulo")
public class ArticuloController extends BaseControllerImpl<Articulo, ArticuloServiceImp> {

}
