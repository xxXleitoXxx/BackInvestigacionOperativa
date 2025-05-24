package org.example.services.clasesImp;

import org.example.entity.VentaArticulo;
import org.example.repository.BaseRepository;
import org.example.repository.VentaArticuloRepository;
import org.example.services.BaseService;
import org.example.services.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class VentaArticuloImp extends BaseServiceImpl <VentaArticulo,Long> implements BaseService<VentaArticulo,Long> {

    //Repositorio
    @Autowired
    VentaArticuloRepository ventaArticuloRepository;

    public VentaArticuloImp(BaseRepository<VentaArticulo, Long> baseRespository, VentaArticuloRepository ventaArticuloRepository) {
        super(baseRespository);
        this.ventaArticuloRepository = ventaArticuloRepository;
    }
}
