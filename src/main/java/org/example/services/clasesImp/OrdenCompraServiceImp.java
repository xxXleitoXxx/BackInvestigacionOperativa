package org.example.services.clasesImp;

import jakarta.transaction.Transactional;
import org.example.entity.OrdenCompra;
import org.example.repository.BaseRepository;
import org.example.repository.OrdenCompraRepository;
import org.example.services.BaseServiceImpl;
import org.example.services.interfaces.OrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenCompraServiceImp extends BaseServiceImpl<OrdenCompra,Long> implements OrdenCompraService {

    @Autowired
    OrdenCompraRepository ordenCompraRepository;

    public OrdenCompraServiceImp(BaseRepository<OrdenCompra, Long> baseRespository, OrdenCompraRepository ordenCompraRepository) {
        super(baseRespository);
        this.ordenCompraRepository = ordenCompraRepository;
    }

    //Métodos
    @Transactional
    public List<OrdenCompra> buscarOrdenCompraPorEstado (String codigoEstado){

        return ordenCompraRepository.findByEstadoCodigo(codigoEstado);

    }

}
