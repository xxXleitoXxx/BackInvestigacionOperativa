package org.example.services.clasesImp;

import jakarta.transaction.Transactional;
import org.example.entity.Proveedor;
import org.example.repository.BaseRepository;
import org.example.repository.ProveedorRepository;
import org.example.services.BaseServiceImpl;
import org.example.services.interfaces.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorServiceImp extends BaseServiceImpl<Proveedor,Long> implements ProveedorService {

    //Repositorio
    @Autowired
    ProveedorRepository proveedorRepository;

    //Constructor
    public ProveedorServiceImp(BaseRepository<Proveedor, Long> baseRespository, ProveedorRepository proveedorRepository) {
        super(baseRespository);
        this.proveedorRepository = proveedorRepository;
    }


    //Lo uso en maestro de artículos.
    @Override
    @Transactional
    public List<Proveedor> findProveedoresActivosByArticuloId(Long articuloId) {
        return proveedorRepository.findProveedoresActivosByArticuloId(articuloId);
    }
}
