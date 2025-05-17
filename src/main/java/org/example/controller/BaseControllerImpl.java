package org.example.controller;


import org.example.entity.BaseEntity;
import org.example.services.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


public abstract class BaseControllerImpl<E extends BaseEntity, S extends BaseServiceImpl<E,Long>> implements BaseController<E,Long> {

    @Autowired
    protected S servicio;


    //nos devuelve una lista de entidades o error, por eso usamos el "?"
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        try {
            //Si el status da como respuesta "ok" nos da un 200
            //Body lo convierte a Json
            return ResponseEntity.status(HttpStatus.OK).body(servicio.findAll());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\" Error\"}");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.findById(id));
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\" Error\"}");
        }

    }

    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody E entity){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.save(entity));
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }

    @PutMapping("")
    public ResponseEntity<?> saveAll(@RequestBody List<E> entity) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.saveAll(entity));
        } catch (Exception e) {
            throw new Exception("Error al guardar los datos: " + e.getMessage());

        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable  Long id, @RequestBody E entity){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.update(id, entity));
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(servicio.delete(id));
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\" Error\"}");
        }
    }

}