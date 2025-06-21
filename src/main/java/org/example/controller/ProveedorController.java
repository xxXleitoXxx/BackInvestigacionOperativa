package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.entity.Proveedor;
import org.example.services.clasesImp.ProveedorServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/Proveedor")
public class ProveedorController extends BaseControllerImpl<Proveedor, ProveedorServiceImp> {
    @Override
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        try {
            //Si el status da como respuesta "ok" nos da un 200
            //Body lo convierte a Json
            return ResponseEntity.status(HttpStatus.OK).body(servicio.getProveedores());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\" Error\"}");
        }
    }


}
