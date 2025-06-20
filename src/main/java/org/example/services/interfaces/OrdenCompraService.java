package org.example.services.interfaces;

import org.example.entity.OrdenCompra;
import org.example.services.BaseService;

import java.util.List;

public interface OrdenCompraService extends BaseService<OrdenCompra,Long> {

    //Busca todas las órdenes de compra por el código de Estado.
    public List<OrdenCompra> buscarOrdenCompraPorEstado (String codigoEstado);
}
