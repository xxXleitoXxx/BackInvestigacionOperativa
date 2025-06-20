package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.entity.Articulo;
import org.example.services.clasesImp.ArticuloServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/Articulo")
public class ArticuloController extends BaseControllerImpl<Articulo, ArticuloServiceImp> {

        //Alta Articulo
        @PostMapping("/altaArticulo")
        public ResponseEntity<?> altaArticulo(@RequestBody Articulo articulo) {

            // Validaciones de formato

            //Código nulo, en blanco
            if (articulo.getCodArt() == null || articulo.getCodArt().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El código del artículo es obligatorio."));
            }

            //Nombre nulo o en blanco
            if (articulo.getNomArt() == null || articulo.getNomArt().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El nombre del artículo es obligatorio."));
            }

            //Descripción nula o en blanco
            if (articulo.getDescripcionArt() == null || articulo.getDescripcionArt().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La descripción del artículo es obligatoria."));
            }

            //Precio de venta menor a 0
            if (articulo.getPrecioVenta() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "El precio de venta debe ser mayor a cero."));
            }
            try {
                Articulo nuevoArticulo = servicio.altaArticulo(articulo);
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevoArticulo);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
            }
        }

        //BajaArticulo
        @PutMapping("/bajaArticulo")
        public ResponseEntity<?> bajaArticulo(@RequestBody Articulo articulo ){
            try{
                Articulo articuloModificado = servicio.bajaArticulo(articulo);
                return ResponseEntity.status(HttpStatus.OK).body(articuloModificado);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo dar de baja el artículo");
            }
        }

        //ModificarArticulo
        @PutMapping("/modificar")
        public ResponseEntity<?> modificarArticulo(@RequestBody Articulo articulo) {
            try {
                Articulo actualizado = servicio.modificarArticulo(articulo);
                return ResponseEntity.ok(actualizado);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }

}
