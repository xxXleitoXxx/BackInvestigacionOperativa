package org.example.controller;

import org.example.entity.Articulo;
import org.example.services.ArticuloServiceImp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/articulo")
public class ArticuloController extends BaseControllerImpl<Articulo, ArticuloServiceImp> {
}
