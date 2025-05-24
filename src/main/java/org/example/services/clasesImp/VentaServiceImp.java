package org.example.services.clasesImp;

import org.example.entity.Venta;
import org.example.repository.BaseRepository;
import org.example.repository.VentaRepository;
import org.example.services.BaseService;
import org.example.services.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentaServiceImp extends BaseServiceImpl <Venta,Long> implements BaseService <Venta,Long> {

    //Repositorio
    @Autowired
    VentaRepository ventaRepository;

    //Constructor
    public VentaServiceImp(BaseRepository<Venta, Long> baseRespository, VentaRepository ventaRepository) {
        super(baseRespository);
        this.ventaRepository = ventaRepository;
    }
}
