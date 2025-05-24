package org.example.services.clasesImp;

import org.example.entity.OrdenCompraArticulo;
import org.example.repository.BaseRepository;
import org.example.repository.OrdenCompraArticuloRepository;
import org.example.services.BaseServiceImpl;
import org.example.services.interfaces.OrdenCompraArticuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdenCompraArticuloServiceImp extends BaseServiceImpl<OrdenCompraArticulo,Long> implements OrdenCompraArticuloService {

   @Autowired
    OrdenCompraArticuloRepository ordenCompraArticuloRepository;

    public OrdenCompraArticuloServiceImp(BaseRepository<OrdenCompraArticulo, Long> baseRespository,OrdenCompraArticuloRepository ordenCompraArticuloRepository) {
        super(baseRespository);
        this.ordenCompraArticuloRepository = ordenCompraArticuloRepository;
    }

}
