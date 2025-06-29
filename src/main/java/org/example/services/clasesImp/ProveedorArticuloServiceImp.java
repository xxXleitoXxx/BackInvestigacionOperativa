package org.example.services.clasesImp;
import jakarta.transaction.Transactional;
import org.example.entity.ProveedorArticulo;
import org.example.repository.BaseRepository;
import org.example.repository.ProveedorArticuloRepository;
import org.example.services.BaseServiceImpl;
import org.example.services.interfaces.ProveedorArticuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorArticuloServiceImp extends BaseServiceImpl <ProveedorArticulo,Long> implements ProveedorArticuloService {

    //Repositorio
    @Autowired
    ProveedorArticuloRepository proveedorArticuloRepository;

    //Constructor
    public ProveedorArticuloServiceImp(BaseRepository<ProveedorArticulo, Long> baseRespository, ProveedorArticuloRepository proveedorArticuloRepository) {
        super(baseRespository);
        this.proveedorArticuloRepository = proveedorArticuloRepository;
    }


    @Override
    @Transactional
    public List<ProveedorArticulo> findByFechaHoraBajaArtProvIsNull() {
        return proveedorArticuloRepository.findByFechaHoraBajaArtProvIsNull();
    }

    //buscarInstanciaActivaProveedorArticuloSegunProveedorYArticulo
    @Override
    @Transactional
    public  Optional<ProveedorArticulo> buscarInstanciaActivaProveedorArticuloSegunProveedorYArticulo(Long idProveedor, Long idArticulo){
        return proveedorArticuloRepository.buscarInstanciaActivaProveedorArticuloSegunProveedorYArticulo(idProveedor,idArticulo);
    }

    @Override
    @Transactional
    public List<ProveedorArticulo> findActivosByArticuloId(Long articuloId) {
        return proveedorArticuloRepository.findActivosByArticuloId(articuloId);
    }


}
