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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    //ModificarArticulo
    @PutMapping("/modificarArticulo")
    public ResponseEntity<?> modificarArticulo(@RequestBody @Valid ArticuloDTO articulo) {
        try {
            System.out.println("antes del servicio");
            ArticuloDTO actualizado = servicio.modificarArticulo(articulo);
            System.out.println("despues del servicio");
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/modificarParametrosInventario")
    public ResponseEntity<?> modificarParametrosInventario(@RequestBody @Valid ArticuloDTO articuloDTO) {
        try {
            ArticuloDTO articuloModificado = servicio.modificarParametrosInventario(articuloDTO);
            return ResponseEntity.ok(articuloModificado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    //GET

    //getAllArticulo
    @Override
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.obtenerTodos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    //listarProveedoresActivosPorArticulo
    @GetMapping("/proveedoresPorArticlo")
    public ResponseEntity<?> listarProveedoresActivosPorArticulo(@RequestBody @Valid ArticuloDTO articuloDTO) {
        try {
            List<ProveedorDTO> proveedores = servicio.listarProveedoresPorArticulo(articuloDTO);
            return ResponseEntity.ok(proveedores);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    //listarProductosFaltantes
    @GetMapping("/articulosFaltantes")
    public ResponseEntity<?> listarProductosFaltantes() {
        try {
            List<ArticuloDTO> articulosFaltantes = servicio.listarArticulosFaltantes();
            return ResponseEntity.ok(articulosFaltantes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/productosAReponer")
    public ResponseEntity<?> listarProductosAReponer() {
        List<ArticuloDTO> listaDTO = servicio.listarProductosAReponer();
        return ResponseEntity.ok(listaDTO);
    }





}
