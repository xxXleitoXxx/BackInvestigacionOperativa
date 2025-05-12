package org.example.services;

import org.example.entity.Alumno;
import org.example.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AlumnoService {
    @Autowired
    private AlumnoRepository alumnoRepository;

    public List<Alumno> ObtenerAlumnosByDni(int dni){
       return alumnoRepository.findByDniAlumno(dni);
    }

    public Alumno GuardarAlumno(Alumno alumno){return alumnoRepository.save(alumno);}

    public List<Alumno> ObtenerTodosAlumnos(){ return alumnoRepository.findAll();}
}
