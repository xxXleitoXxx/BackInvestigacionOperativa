package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.dto.OrdenCompraDTO;
import org.example.dto.VentaDTO;
import org.example.entity.Venta;
import org.example.services.clasesImp.VentaServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/Venta")
public class VentaController extends BaseControllerImpl<Venta, VentaServiceImp> {

    @Autowired
    private VentaServiceImp ventaServiceImp;

    @GetMapping("/get")
    public ResponseEntity<?>getVentas(){
        try {
            List<VentaDTO> lista = ventaServiceImp.listarOC();
            return ResponseEntity.ok(lista);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body( e.getMessage());
        }

    }



    @PostMapping("/vent")
    public ResponseEntity<?> guardarVenta(@RequestBody  VentaDTO ventaDTO){
        try {
            System.out.println("A");
            //System.out.println(vent.getVentaArticulos());
            //System.out.println(vent.getMontoTotalVent());
            boolean ventaP = this.ventaServiceImp.controlVenta(ventaDTO);
            if (ventaP == true) {
                System.out.println("B");
                ventaServiceImp.ActualizarStock(ventaDTO);
                System.out.println("C");
                Venta vent = ventaServiceImp.dtoAventa(ventaDTO);
                ventaServiceImp.save(vent);
                System.out.println("D");
                return ResponseEntity.status(HttpStatus.OK).body("se guardo la venta");
            } else {
                throw new Exception("La venta supera el stock");
               // return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se puede concretar esta venta");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



}
