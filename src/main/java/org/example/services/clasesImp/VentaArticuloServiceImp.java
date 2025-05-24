package org.example.services.clasesImp;

import org.example.entity.VentaArticulo;
import org.example.repository.BaseRepository;
import org.example.repository.VentaArticuloRepository;
import org.example.services.BaseService;
import org.example.services.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentaArticuloServiceImp extends BaseServiceImpl <VentaArticulo,Long> implements BaseService<VentaArticulo,Long> {

    //Repositorio
    @Autowired
    VentaArticuloRepository ventaArticuloRepository;

    public VentaArticuloServiceImp(BaseRepository<VentaArticulo, Long> baseRespository, VentaArticuloRepository ventaArticuloRepository) {
        super(baseRespository);
        this.ventaArticuloRepository = ventaArticuloRepository;
    }
}
