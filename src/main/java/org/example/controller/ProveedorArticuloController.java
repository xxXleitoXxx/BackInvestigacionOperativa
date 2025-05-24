package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.entity.ProveedorArticulo;
import org.example.services.clasesImp.ProveedorArticuloServiceImp;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/ProveedorArticuloController")
public class ProveedorArticuloController extends BaseControllerImpl<ProveedorArticulo, ProveedorArticuloServiceImp> {


}
