package org.example.services.clasesImp;

import org.example.entity.EstadoOrdenCompra;
import org.example.repository.BaseRepository;
import org.example.repository.EstadoOrdenCompraRepository;
import org.example.services.BaseServiceImpl;
import org.example.services.interfaces.EstadoOrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstadoOrdenCompraServiceImp extends BaseServiceImpl<EstadoOrdenCompra,Long> implements EstadoOrdenCompraService {

    @Autowired
    EstadoOrdenCompraRepository estadoOrdenCompraRepository;

    public EstadoOrdenCompraServiceImp(BaseRepository<EstadoOrdenCompra, Long> baseRespository, EstadoOrdenCompraRepository estadoOrdenCompraRepository) {
        super(baseRespository);
        this.estadoOrdenCompraRepository = estadoOrdenCompraRepository;
    }

}
