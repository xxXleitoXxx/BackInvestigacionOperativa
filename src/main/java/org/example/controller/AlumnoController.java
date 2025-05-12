package org.example.controller;

import org.example.entity.Alumno;
import org.example.repository.AlumnoRepository;
import org.example.services.AlumnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;
    @GetMapping
    public List<Alumno> ObtenerTodosAlumnos(){
        return alumnoService.ObtenerTodosAlumnos();
    }

    @GetMapping("/apellido/{dni}")
    public List<Alumno> ObtenerAlumnosPorDni(@PathVariable int dni) {
        return alumnoService.ObtenerAlumnosByDni(dni);
    }
    @PostMapping
    public Alumno agregarAlumno (@RequestBody Alumno alumno){
        return alumnoService.GuardarAlumno(alumno);
    }

}
