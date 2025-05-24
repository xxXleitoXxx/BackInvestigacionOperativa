package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.entity.EstadoOrdenCompra;
import org.example.services.clasesImp.EstadoOrdenCompraServiceImp;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin (origins = "*")
@RequestMapping(path = "/EstadoOrdenCompra")
public class EstadoOrdenCompraController extends BaseControllerImpl<EstadoOrdenCompra, EstadoOrdenCompraServiceImp> {


}
