package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.entity.OrdenCompraArticulo;
import org.example.services.clasesImp.OrdenCompraArticuloServiceImp;
import org.example.services.clasesImp.OrdenCompraServiceImp;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/OrdenCompraArticulo")
public class OrdenCompraArticuloController extends BaseControllerImpl<OrdenCompraArticulo, OrdenCompraArticuloServiceImp> {


}
