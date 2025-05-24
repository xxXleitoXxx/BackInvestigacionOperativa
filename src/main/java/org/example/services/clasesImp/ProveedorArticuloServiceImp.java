package org.example.services.clasesImp;


import org.example.entity.ProveedorArticulo;
import org.example.repository.BaseRepository;
import org.example.repository.ProveedorArticuloRepository;
import org.example.services.BaseService;
import org.example.services.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProveedorArticuloServiceImp extends BaseServiceImpl <ProveedorArticulo,Long> implements BaseService<ProveedorArticulo,Long> {

    //Repositorio
    @Autowired
    ProveedorArticuloRepository proveedorArticuloRepository;

    //Constructor
    public ProveedorArticuloServiceImp(BaseRepository<ProveedorArticulo, Long> baseRespository, ProveedorArticuloRepository proveedorArticuloRepository) {
        super(baseRespository);
        this.proveedorArticuloRepository = proveedorArticuloRepository;
    }
}
