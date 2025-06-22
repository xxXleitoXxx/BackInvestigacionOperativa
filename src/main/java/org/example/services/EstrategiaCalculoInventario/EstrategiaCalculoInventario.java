package org.example.services.EstrategiaCalculoInventario;

import org.example.entity.ProveedorArticulo;
import org.example.enums.TipoLote;

public interface EstrategiaCalculoInventario {

    ProveedorArticulo calcular(ProveedorArticulo proveedorArticulo);
    TipoLote getTipoLote();


}
