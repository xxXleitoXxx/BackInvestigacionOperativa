package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.entity.OrdenCompra;
import org.example.services.clasesImp.OrdenCompraServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/OrdenCompra")
public class OrdenCompraController extends BaseControllerImpl<OrdenCompra, OrdenCompraServiceImp> {


    @Autowired
    private OrdenCompraServiceImp ordenCompraServiceImp;



    @PostMapping("/crearManuel")
    public ResponseEntity<?> crear(@RequestBody OrdenCompra ordenCompra){
        try {
            Boolean PuedoCrear = this.ordenCompraServiceImp.crear(ordenCompra);
            if (PuedoCrear = true){
            return ResponseEntity.status(HttpStatus.OK).body(servicio.save(ordenCompra));
            }else{
                return ResponseEntity.status(HttpStatus.OK).body("No se peude crear esta Orden de Compra");
            }
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }

    @PostMapping("/mod")
    public ResponseEntity<?> mod(@RequestBody OrdenCompra ordenCompra){
        try {
            Boolean PuedoMod = this.ordenCompraServiceImp.mod(ordenCompra);
            if (PuedoMod = true){
            return ResponseEntity.status(HttpStatus.OK).body(servicio.save(ordenCompra));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body("no se puede modificar esta Orden de Compra");
            }
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }


    @PostMapping("/cancelar")
    public ResponseEntity<?> cancelar(@RequestBody OrdenCompra ordenCompra){
        try {
            Boolean PuedoCancelar = this.ordenCompraServiceImp.cancelar(ordenCompra);
            if (PuedoCancelar = true){
            return ResponseEntity.status(HttpStatus.OK).body(servicio.save(ordenCompra));
            }else {
                return ResponseEntity.status(HttpStatus.OK).body("No se puede Cancelar esta Orden de Compra");
            }
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }


    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizar(@RequestBody OrdenCompra ordenCompra){
        try {

            Boolean PuedoFinalizar = this.ordenCompraServiceImp.finalizar(ordenCompra);

            if (PuedoFinalizar == true){
                ordenCompraServiceImp.actualizarStock(ordenCompra);
            return ResponseEntity.status(HttpStatus.OK).body(servicio.save(ordenCompra));
            }else return ResponseEntity.status(HttpStatus.OK).body("No podes Finalizar esta Orden de Compra");
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }

    @PostMapping("/crearPeriodoFijo")
    public ResponseEntity<?> CrearOCPeriodoFijo(){
        try {

            return ResponseEntity.status(HttpStatus.OK).body("se crearon con exito");
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }





}
