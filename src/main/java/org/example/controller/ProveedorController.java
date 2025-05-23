package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.entity.Proveedor;
import org.example.services.clasesImp.ProveedorServiceImp;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/Proveedor")
public class ProveedorController extends BaseControllerImpl<Proveedor, ProveedorServiceImp> {


}
