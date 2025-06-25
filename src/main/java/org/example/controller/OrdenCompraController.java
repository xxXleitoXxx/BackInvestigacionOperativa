package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.dto.OrdenCompraDTO;
import org.example.entity.OrdenCompra;
import org.example.services.clasesImp.OrdenCompraServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/OrdenCompra")
public class OrdenCompraController extends BaseControllerImpl<OrdenCompra, OrdenCompraServiceImp> {


    @Autowired
    private OrdenCompraServiceImp ordenCompraServiceImp;


    @GetMapping("/get")
    public ResponseEntity<?> getAll() {
        try {
            List<OrdenCompraDTO> lista = ordenCompraServiceImp.listarOC();
            return ResponseEntity.ok(lista);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body( e.getMessage());
        }
    }


    @PostMapping("/crearManual")
    public ResponseEntity<?> crear(@RequestBody OrdenCompraDTO ordenCompra){
        try {
            Boolean PuedoCrear = this.ordenCompraServiceImp.crear(ordenCompra);
            if (PuedoCrear = true){
                OrdenCompra ordenCompraE = new OrdenCompra();
                ordenCompraE = ordenCompraServiceImp.DTOaOCNEW(ordenCompra);
            return ResponseEntity.status(HttpStatus.OK).body(servicio.save(ordenCompraE));
            }else{
                return ResponseEntity.status(HttpStatus.OK).body("No se peude crear esta Orden de Compra");
            }
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }

    @PutMapping("/mod")
    public ResponseEntity<?> mod(@RequestBody OrdenCompraDTO ordenCompra){
        try {
            Boolean PuedoMod = this.ordenCompraServiceImp.mod(ordenCompra);
            if (PuedoMod = true){
                OrdenCompra ordenCompraE = new OrdenCompra();
                ordenCompraE = ordenCompraServiceImp.DTOaOC(ordenCompra);
            return ResponseEntity.status(HttpStatus.OK).body(servicio.save(ordenCompraE));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body("no se puede modificar esta Orden de Compra");
            }
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }


    @PutMapping("/cancelar")
    public ResponseEntity<?> cancelar(@RequestBody OrdenCompraDTO ordenCompra){
        try {
            Boolean PuedoCancelar = this.ordenCompraServiceImp.cancelar(ordenCompra);
            System.out.println(PuedoCancelar);
            if (PuedoCancelar == true){
                OrdenCompra ordenCompraE = new OrdenCompra();
                ordenCompraE = ordenCompraServiceImp.DTOaOC(ordenCompra);
            return ResponseEntity.status(HttpStatus.OK).body(servicio.save(ordenCompraE));
            }else {
                return ResponseEntity.status(HttpStatus.OK).body("No se puede Cancelar esta Orden de Compra");
            }
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }


    @PutMapping("/finalizar")
    public ResponseEntity<?> finalizar(@RequestBody OrdenCompraDTO ordenCompra){
        try {

            Boolean PuedoFinalizar = this.ordenCompraServiceImp.finalizar(ordenCompra);

            if (PuedoFinalizar == true){
                ordenCompraServiceImp.actualizarStock(ordenCompra);
                OrdenCompra ordenCompraE = new OrdenCompra();
                ordenCompraE = ordenCompraServiceImp.DTOaOC(ordenCompra);
            return ResponseEntity.status(HttpStatus.OK).body(servicio.save(ordenCompraE));
            }else return ResponseEntity.status(HttpStatus.OK).body("No podes Finalizar esta Orden de Compra");
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }

    @PutMapping("/crearPeriodoFijo")
    public ResponseEntity<?> CrearOCPeriodoFijo(){
        try {
            System.out.println("A");
            ordenCompraServiceImp.crearporPeriodoFijo();
            System.out.println("B");
            return ResponseEntity.status(HttpStatus.OK).body("se crearon con exito");
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }





}
