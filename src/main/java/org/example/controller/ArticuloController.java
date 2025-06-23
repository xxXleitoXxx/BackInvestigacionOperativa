package org.example.controller;

import jakarta.validation.Valid;
import org.example.controller.Bases.BaseControllerImpl;
import org.example.dto.ArticuloDTO;
import org.example.dto.ProveedorDTO;
import org.example.entity.Articulo;
import org.example.entity.Proveedor;
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
    //Alta
    @PostMapping("/altaArticulo")
    public ResponseEntity<?> altaArticulo(@RequestBody @Valid ArticuloDTO dto) {
        try {
            ArticuloDTO nuevoArticulo = servicio.altaArticulo(dto); // se pasa el DTO directo
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoArticulo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    //PUT

    //BajaArticulo
    @PutMapping("/bajaArticulo")
    public ResponseEntity<?> bajaArticulo(@RequestBody @Valid ArticuloDTO articuloDTO){
        try{
            ArticuloDTO articuloRetornoDTO = servicio.bajaArticulo(articuloDTO);
            return ResponseEntity.status(HttpStatus.OK).body(articuloRetornoDTO);
        } catch (Exception e) {
            // Manejo de excepciones, puedes ser más específico si es necesario
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo dar de baja el artículo: " + e.getMessage());
        }
    }

    //ModificarArticulo
    @PutMapping("/modificarArticulo")
    public ResponseEntity<?> modificarArticulo(@RequestBody @Valid ArticuloDTO articulo) {
        try {
            ArticuloDTO actualizado = servicio.modificarArticulo(articulo);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/modificarParametrosInventario")
    public ResponseEntity<ArticuloDTO> modificarParametrosInventario(@RequestBody @Valid ArticuloDTO articuloDTO) {
        try {
            ArticuloDTO articuloModificado = servicio.modificarParametrosInventario(articuloDTO);
            return ResponseEntity.ok(articuloModificado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //GET

    //listarArticulosActivos
    @GetMapping("/articulosActivos")
    public ResponseEntity<?> listarArticulosActivos() {
        try {
            List<ArticuloDTO> articulosActivosDTO = servicio.listarArticulosActivos();
            return ResponseEntity.ok(articulosActivosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener artículos activos."));
        }
    }

    //listarArticulosDadosDeBaja
    @GetMapping("/articulosDadosDeBaja")
    public ResponseEntity<?> listarArticulosDadosDeBaja() {
        try {
            List<ArticuloDTO> dadosDeBaja = servicio.listarArticulosDadosDeBaja();
            return ResponseEntity.ok(dadosDeBaja);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener artículos dados de baja."));
        }
    }

    //listarProveedoresActivosPorArticulo
    @GetMapping("/proveedoresPorArticlo")
    public ResponseEntity<?> listarProveedoresActivosPorArticulo(@RequestBody @Valid ArticuloDTO articuloDTO) {
        try {
            List<ProveedorDTO> proveedores = servicio.listarProveedoresPorArticulo(articuloDTO);
            return ResponseEntity.ok(proveedores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"No se pudo obtener la lista de proveedores: " + e.getMessage() + "\"}");
        }
    }

    //listarProductosFaltantes
    @GetMapping("/articulosFaltantes")
    public ResponseEntity<?> listarProductosFaltantes() {
        try {
            List<ArticuloDTO> articulosFaltantes = servicio.listarArticulosFaltantes();
            return ResponseEntity.ok(articulosFaltantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"No se pudo obtener la lista de artículos faltantes: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/productosAReponer")
    public ResponseEntity<List<ArticuloDTO>> listarProductosAReponer() {
        List<ArticuloDTO> listaDTO = servicio.listarProductosAReponer();
        return ResponseEntity.ok(listaDTO);
    }



}
