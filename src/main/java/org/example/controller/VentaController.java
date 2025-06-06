package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.entity.Venta;
import org.example.services.clasesImp.VentaServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/Venta")
public class VentaController extends BaseControllerImpl<Venta, VentaServiceImp> {

    @Autowired
    private VentaServiceImp ventaServiceImp;


    @PostMapping("/vent")
    public ResponseEntity<?> guardarVenta(@RequestBody  Venta venta){
        try {
            System.out.println("hola");
            System.out.println("holaaass");
            boolean ventaP = this.ventaServiceImp.controlVenta(venta);
            System.out.println("holaaa");
            if (ventaP == true) {
                System.out.println("dddd");
                ventaServiceImp.ActualizarStock(venta);
                ventaServiceImp.save(venta);
                return ResponseEntity.ok("savedVenta");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se puede concretar esta venta");
            }
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }



}
