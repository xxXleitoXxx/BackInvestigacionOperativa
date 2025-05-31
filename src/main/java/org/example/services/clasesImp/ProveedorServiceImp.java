package org.example.services.clasesImp;

import org.example.entity.Proveedor;
import org.example.repository.BaseRepository;
import org.example.repository.ProveedorRepository;
import org.example.services.BaseService;
import org.example.services.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProveedorServiceImp extends BaseServiceImpl<Proveedor,Long> implements BaseService<Proveedor,Long> {

    //Repositorio
    @Autowired
    ProveedorRepository proveedorRepository;

    //Constructor
    public ProveedorServiceImp(BaseRepository<Proveedor, Long> baseRespository, ProveedorRepository proveedorRepository) {
        super(baseRespository);
        this.proveedorRepository = proveedorRepository;
    }


}
