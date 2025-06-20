package org.example.services.interfaces;

import org.example.entity.Proveedor;
import org.example.services.BaseService;


import java.util.List;


public interface ProveedorService extends BaseService<Proveedor,Long> {

    //findProveedoresActivosByArticuloId: Busca a los proveedores activos filtrados por artículo.
    List<Proveedor> findProveedoresActivosByArticuloId(Long articuloid);


}
