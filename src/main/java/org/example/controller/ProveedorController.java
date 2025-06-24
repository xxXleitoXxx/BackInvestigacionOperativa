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
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/Proveedor")
public class ProveedorController extends BaseControllerImpl<Proveedor, ProveedorServiceImp> {

    //Post

    //altaProveedor
    @PostMapping("/altaProveedor")
    public ResponseEntity<?> crearProveedor(@RequestBody  ProveedorDTO proveedorDTO) {
        try {
            ProveedorDTO proveedorCreado = servicio.altaProveedor(proveedorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(proveedorCreado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // o .body(null)
        }
    }

    //bajaProveedor
    @PutMapping("/bajaProveedor")
    public ResponseEntity<?> bajaProveedor(@RequestBody @Valid ProveedorDTO proveedorDTO) {
        try {
            ProveedorDTO proveedorBaja = servicio.bajaProveedor(proveedorDTO);
            return ResponseEntity.ok(proveedorBaja);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/modificarProveedor")
    public ResponseEntity<?> modificarProveedor(@RequestBody @Valid ProveedorDTO proveedorDTO) {
        try {
            ProveedorDTO proveedorActualizado = servicio.modificarProveedor(proveedorDTO);
            return ResponseEntity.ok(proveedorActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    //Get
    //listarArticulosPorProveedor
    @PostMapping("/listarArticulosPorProveedor")
    public ResponseEntity<?> listarArticulosPorProveedor(@RequestBody @Valid ProveedorDTO proveedorDTO) {
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
            return ResponseEntity.status(HttpStatus.OK).body(servicio.obtenerTodosLosProveedores());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\" Error\"}");
        }
    }


}
