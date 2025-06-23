package org.example.services.interfaces;

import org.example.dto.ProveedorDTO;
import org.example.entity.Proveedor;
import org.example.entity.ProveedorArticulo;
import org.example.services.BaseService;


import java.util.List;
import java.util.Optional;


public interface ProveedorService extends BaseService<Proveedor,Long> {

    //findByCodProv
    Optional<Proveedor> findByCodProv(String codProv);

    //findProveedoresActivosByArticuloId: Busca a los proveedores activos filtrados por artículo.
    List<Proveedor> findProveedoresActivosByArticuloId(Long articuloid);





}
