package org.example.services;

import org.example.entity.Articulo;
import org.example.repository.ArticuloRepository;
import org.example.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticuloServiceImp extends BaseServiceImpl<Articulo,Long> implements ArticuloService{

    @Autowired
    ArticuloRepository articuloRepository;

    public ArticuloServiceImp(BaseRepository<Articulo, Long> baseRespository, ArticuloRepository clienteRepository) {
        super(baseRespository);
        this.articuloRepository = articuloRepository;
    }



}
