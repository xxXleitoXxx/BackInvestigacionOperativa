package org.example.services.clasesImp;

import org.example.enums.TipoLote;
import org.example.entity.*;
import org.example.repository.*;
import org.example.services.BaseServiceImpl;
import org.example.services.interfaces.OrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenCompraServiceImp extends BaseServiceImpl<OrdenCompra,Long> implements OrdenCompraService {

    @Autowired
    OrdenCompraRepository ordenCompraRepository;
    @Autowired
    ArticuloRepository articuloRepository;
    @Autowired
    ProveedorRepository proveedorRepository;

    public OrdenCompraServiceImp(BaseRepository<OrdenCompra, Long> baseRespository, OrdenCompraRepository ordenCompraRepository) {
        super(baseRespository);
        this.ordenCompraRepository = ordenCompraRepository;
    }

    public Boolean mod(OrdenCompra ordenCompra) {
        boolean posible = false;
        OrdenCompra oc = ordenCompraRepository.findById(ordenCompra.getId()).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + ordenCompra.getId()));
        if (oc.getEstadoOrdCom().getNomEOC() == "Pendiente"){
            posible = true;
        }
        return posible;
    }

    public Boolean crear(OrdenCompra ordenCompra) {
        boolean PuedoCrear = true;

        if (ordenCompra.getEstadoOrdCom().getNomEOC() != "Pendiente"){
           PuedoCrear = false;
        }

            Articulo art = ordenCompra.getArticulo();

            for (OrdenCompra oc : ordenCompraRepository.findAll()) {
                    if ((ordenCompra.getArticulo() == art) && (oc.getEstadoOrdCom().getNomEOC() != "Finalizada")) {
                        PuedoCrear = false;
                        break;
                    }

                break;
            }

            Proveedor prov = proveedorRepository.findById(art.getProveedorElegido().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + art.getProveedorElegido().getId() ));
            for (ProveedorArticulo pa : prov.getProveedorArticulos()){
                if ( prov == art.getProveedorElegido()  ){
                    PuedoCrear = true;
                }
            }





        return PuedoCrear;
    }

    public Boolean cancelar(OrdenCompra ordenCompra) {
        Boolean posible = false;
        OrdenCompra oc = ordenCompraRepository.findById(ordenCompra.getId()).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + ordenCompra.getId()));
        if (oc.getEstadoOrdCom().getNomEOC() != "Pendiente"){
            posible = true;
        }
        return posible;
    }

    public Boolean finalizar(OrdenCompra ordenCompra) {

        Boolean posible = false;
        OrdenCompra oc = ordenCompraRepository.findById(ordenCompra.getId()).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + ordenCompra.getId()));
        if (oc.getEstadoOrdCom().getNomEOC() == "Enviada"){
            posible = true;
        }
        return posible;
    }

    public void actualizarStock(OrdenCompra ordenCompra) {

            int StockNuevo = ordenCompra.getArticulo().getStock() + ordenCompra.getCantPedida();
            Articulo articulo = articuloRepository.findById(ordenCompra.getArticulo().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + ordenCompra.getArticulo().getId()));
            ordenCompra.getArticulo().setStock(StockNuevo);
            Proveedor prov = proveedorRepository.findById(ordenCompra.getArticulo().getProveedorElegido().getId()).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + ordenCompra.getArticulo().getProveedorElegido().getId()));
            for (ProveedorArticulo pa : prov.getProveedorArticulos()){
                if (pa.getArt() == ordenCompra.getArticulo()){
                    if(StockNuevo < pa.getLoteOptimo()){
                        ResponseEntity.status(HttpStatus.OK).body("No llegaste a tu lote optimo");
                    }
                    break;
                }
            }
            articuloRepository.save(articulo);



    }

    public void crearporPeriodoFijo() {
       for ( Articulo art : articuloRepository.findAll()){

           Proveedor prov = proveedorRepository.findById(art.getProveedorElegido().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + art.getProveedorElegido().getId() ));
           for (ProveedorArticulo pa : prov.getProveedorArticulos()){
               if ( (pa.getArt() == art) && (pa.tipoLote == TipoLote.LOTEFIJO)   ){

               }
           }

        }

    }

    @Override
    public List<OrdenCompra> buscarOrdenCompraPorEstado(String codigoEstado) {
        return ordenCompraRepository.findByEstadoCodigo(codigoEstado);
    }
}
