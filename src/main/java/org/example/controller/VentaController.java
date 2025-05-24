package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.entity.Venta;
import org.example.services.clasesImp.VentaServiceImp;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/Venta")
public class VentaController extends BaseControllerImpl<Venta, VentaServiceImp> {


}
