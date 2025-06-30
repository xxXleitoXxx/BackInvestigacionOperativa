package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.dto.ArticuloDTO;
import org.example.dto.ArticuloOCDTO;
import org.example.dto.OrdenCompraDTO;
import org.example.entity.OrdenCompra;
import org.example.services.clasesImp.OrdenCompraServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
            System.out.println("1");
            Boolean PuedoCrear = this.ordenCompraServiceImp.crear(ordenCompra);
            System.out.println("2");
            if (PuedoCrear){
                System.out.println("3");
                OrdenCompra ordenCompraE = new OrdenCompra();
                System.out.println("4");
                ordenCompraE = ordenCompraServiceImp.DTOaOCNEW(ordenCompra);
                System.out.println("5");
                servicio.save(ordenCompraE);
                return ResponseEntity.status(HttpStatus.OK).body("funciona");
            }else{
               throw new Exception("No se peude crear esta Orden de Compra");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/mod")
    public ResponseEntity<?> mod(@RequestBody OrdenCompraDTO ordenCompra){
        try {
            Boolean PuedoMod = this.ordenCompraServiceImp.mod(ordenCompra);
            System.out.println(PuedoMod);
            if (PuedoMod == true){
                OrdenCompra ordenCompraE = new OrdenCompra();
                ordenCompraE = ordenCompraServiceImp.DTOaOC(ordenCompra);
                servicio.update(ordenCompraE.getId(),ordenCompraE);
            return ResponseEntity.status(HttpStatus.OK).body("funciona");
            } else {
                return ResponseEntity.status(HttpStatus.OK).body("no se puede modificar esta Orden de Compra");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
                servicio.save(ordenCompraE);
            return ResponseEntity.status(HttpStatus.OK).body("Se cancelo con exito");
            }else {

                throw new Exception ("No se puede Cancelar esta Orden de Compra");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/finalizar")
    public ResponseEntity<?> finalizar(@RequestBody OrdenCompraDTO ordenCompra) {
        try {

            Boolean PuedoFinalizar = this.ordenCompraServiceImp.finalizar(ordenCompra);
            System.out.println(PuedoFinalizar);
            if (PuedoFinalizar) {
                ordenCompraServiceImp.actualizarStock(ordenCompra);
                OrdenCompra ordenCompraE = new OrdenCompra();
                ordenCompraE = ordenCompraServiceImp.DTOaOC(ordenCompra);
                servicio.save(ordenCompraE);
                ordenCompraServiceImp.verSiLoteOptimo(ordenCompraE);
                return ResponseEntity.ok(ordenCompraE);

            } else throw new Exception("No podes Finalizar esta Orden de Compra");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/crearPeriodoFijo")
    public ResponseEntity<?> CrearOCPeriodoFijo(){
        try {
            System.out.println("A");
            ordenCompraServiceImp.crearporPeriodoFijo();
            System.out.println("B");
            return ResponseEntity.status(HttpStatus.OK).body("se crearon con exito");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping  ("/Cant")
    public ResponseEntity<?> getcantecomendato(@RequestBody ArticuloOCDTO artOCDTO){
        try {
            int cantidadAPedir = 0;
            cantidadAPedir = ordenCompraServiceImp.cantidadreoc(artOCDTO);
            return ResponseEntity.ok(cantidadAPedir);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }




}
