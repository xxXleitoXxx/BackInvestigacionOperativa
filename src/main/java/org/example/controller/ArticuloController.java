package org.example.controller;

import org.example.controller.Bases.BaseControllerImpl;
import org.example.entity.Articulo;
import org.example.services.clasesImp.ArticuloServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/Articulo")
public class ArticuloController extends BaseControllerImpl<Articulo, ArticuloServiceImp> {

        //Métodos REST

        //Post
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

            //Precio de venta Mayor  a 0
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

        //PUT

        //BajaArticulo
        @PutMapping("/bajaArticulo/{id}")
        public ResponseEntity<?> bajaArticulo(@PathVariable Long id){
            try{
                Articulo articuloBajado = servicio.bajaArticulo(id);
                return ResponseEntity.status(HttpStatus.OK).body(articuloBajado);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo dar de baja el artículo");
            }
        }

        //ModificarArticulo
        @PutMapping("/modificarArticulo/{id}")
        public ResponseEntity<?> modificarArticulo(@RequestBody Articulo articulo, @PathVariable Long id) {
            try {
                Articulo actualizado = servicio.modificarArticulo(articulo, id);
                return ResponseEntity.ok(actualizado);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }

        //GET

        //listarArticulosActivos
        @GetMapping("/articulosActivos")
        public ResponseEntity<?> listarArticulosActivos() {
            try {
                List<Articulo> activos = servicio.listarArticulosActivos();
                return ResponseEntity.ok(activos);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Error al obtener artículos activos."));
            }
        }

        //listarArticulosDadosDeBaja
        @GetMapping("/articulosDadosDeBaja")
        public ResponseEntity<?> listarArticulosDadosDeBaja() {
            try {
                List<Articulo> dadosDeBaja = servicio.listarArticulosDadosDeBaja();
                return ResponseEntity.ok(dadosDeBaja);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Error al obtener artículos dados de baja."));
            }
        }


}
