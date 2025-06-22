package org.example.controller;

import jakarta.validation.Valid;
import org.example.controller.Bases.BaseControllerImpl;
import org.example.dto.ProveedorDTO;
import org.example.entity.Proveedor;
import org.example.services.clasesImp.ProveedorServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/Proveedor")
public class ProveedorController extends BaseControllerImpl<Proveedor, ProveedorServiceImp> {


    @PostMapping("/altaProveedor")
    public ResponseEntity<ProveedorDTO> crearProveedor(@RequestBody @Valid ProveedorDTO proveedorDTO) {
        try {
            ProveedorDTO proveedorCreado = servicio.altaProveedor(proveedorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(proveedorCreado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // o .body(null)
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
