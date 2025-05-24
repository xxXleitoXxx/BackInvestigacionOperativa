package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.entity.VentaArticulo;
import org.example.services.clasesImp.VentaArticuloServiceImp;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/VentaArticulo")
public class VentaArticuloController extends BaseControllerImpl<VentaArticulo, VentaArticuloServiceImp> {


}
