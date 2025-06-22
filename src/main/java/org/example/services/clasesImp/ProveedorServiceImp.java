package org.example.services.clasesImp;

import jakarta.transaction.Transactional;
import org.example.dto.ProveedorDTO;
import org.example.entity.Proveedor;
import org.example.repository.BaseRepository;
import org.example.repository.ProveedorRepository;
import org.example.services.BaseServiceImpl;
import org.example.services.interfaces.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProveedorServiceImp extends BaseServiceImpl<Proveedor,Long> implements ProveedorService {

    //Repositorio
    @Autowired
    ProveedorRepository proveedorRepository;

    //Constructor
    public ProveedorServiceImp(BaseRepository<Proveedor, Long> baseRespository, ProveedorRepository proveedorRepository) {
        super(baseRespository);
        this.proveedorRepository = proveedorRepository;
    }


    //Lo uso en maestro de artículos.
    @Override
    @Transactional
    public List<Proveedor> findProveedoresActivosByArticuloId(Long articuloId) {
        return proveedorRepository.findProveedoresActivosByArticuloId(articuloId);
    }

    public List<ProveedorDTO> getProveedores(){
        List<Proveedor> proveedors = proveedorRepository.findAll();
        List<ProveedorDTO> proveedorDtos= new ArrayList<>();
        for(Proveedor p:proveedors){
         ProveedorDTO pd = new ProveedorDTO();

            pd.setCodProv(p.getCodProv());
            pd.setNomProv(p.getNomProv());
            proveedorDtos.add(pd);
        }
        return proveedorDtos;
    }

}
