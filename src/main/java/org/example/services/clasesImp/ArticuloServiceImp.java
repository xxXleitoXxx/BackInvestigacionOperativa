package org.example.services.clasesImp;

import org.example.entity.Articulo;
import org.example.repository.ArticuloRepository;
import org.example.repository.BaseRepository;
import org.example.services.BaseServiceImpl;
import org.example.services.interfaces.ArticuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticuloServiceImp extends BaseServiceImpl<Articulo,Long> implements ArticuloService {

    @Autowired
    ArticuloRepository articuloRepository;

    public ArticuloServiceImp(BaseRepository<Articulo, Long> baseRespository, ArticuloRepository articuloRepository) {
        super(baseRespository);
        this.articuloRepository = articuloRepository;
    }

}
