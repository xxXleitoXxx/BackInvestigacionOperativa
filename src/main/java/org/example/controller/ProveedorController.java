package org.example.controller;

import jakarta.validation.Valid;
import org.example.controller.Bases.BaseControllerImpl;
import org.example.dto.ArticuloDTO;
import org.example.dto.ProveedorDTO;
import org.example.entity.Proveedor;
import org.example.services.clasesImp.ProveedorServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/Proveedor")
public class ProveedorController extends BaseControllerImpl<Proveedor, ProveedorServiceImp> {


    //Post

    //altaProveedor
    @PostMapping("/altaProveedor")
    public ResponseEntity<ProveedorDTO> crearProveedor(@RequestBody  ProveedorDTO proveedorDTO) {
        try {
            ProveedorDTO proveedorCreado = servicio.altaProveedor(proveedorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(proveedorCreado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // o .body(null)
        }
    }

    //bajaProveedor
    @PutMapping("/bajaProveedor")
    public ResponseEntity<ProveedorDTO> bajaProveedor(@RequestBody ProveedorDTO proveedorDTO) {
        try {
            ProveedorDTO proveedorBaja = servicio.bajaProveedor(proveedorDTO);
            return ResponseEntity.ok(proveedorBaja);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    //Get

    //listarArticulosPorProveedor
    @PostMapping("/listarArticulosPorProveedor")
    public ResponseEntity<List<ArticuloDTO>> listarArticulosPorProveedor(@RequestBody @Valid ProveedorDTO proveedorDTO) {
        try {
            List<ArticuloDTO> articulos = servicio.listarArticulosPorProveedor(proveedorDTO);
            return ResponseEntity.ok(articulos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


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
