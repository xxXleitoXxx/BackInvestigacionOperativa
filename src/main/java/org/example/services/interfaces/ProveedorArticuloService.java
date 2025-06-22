package org.example.services.interfaces;

import org.example.entity.Articulo;
import org.example.entity.Proveedor;
import org.example.entity.ProveedorArticulo;
import org.example.services.BaseService;

import java.util.List;
import java.util.Optional;

public interface ProveedorArticuloService extends BaseService<ProveedorArticulo,Long> {

    List<ProveedorArticulo> findByFechaHoraBajaArtProvIsNull();

}
