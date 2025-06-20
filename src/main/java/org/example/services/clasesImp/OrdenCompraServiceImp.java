package org.example.services.clasesImp;

import jakarta.transaction.Transactional;
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
    OrdenCompraArticuloRepository ordenCompraArticuloRepository;
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
        for (OrdenCompraArticulo ocaI : ordenCompra.getOrdenCompraArticulo()) {
            Articulo art = ocaI.getArt();
            for (OrdenCompra oc : ordenCompraRepository.findAll()) {
                for (OrdenCompraArticulo oca : ordenCompraArticuloRepository.findAll()) {
                    if ((oca.getArt() == art) && (oc.getEstadoOrdCom().getNomEOC() != "Finalizada")) {
                        PuedoCrear = false;
                        break;
                    }
                }
                break;
            }
            break;
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

        for (OrdenCompraArticulo oca : ordenCompra.getOrdenCompraArticulo()){
            int StockNuevo = oca.getArt().getStock() + oca.getCantArtPedida();
            oca.getArt().setStock(StockNuevo);
            Proveedor prov = proveedorRepository.findById(oca.getArt().getProveedorElegido().getId()).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + oca.getArt().getProveedorElegido().getId()));
            for (ProveedorArticulo pa : prov.getProveedorArticulos()){
                if (pa.getArt() == oca.getArt()){
                    if(StockNuevo < pa.getArt().getLoteOptimo()){
                        ResponseEntity.status(HttpStatus.OK).body("No llegaste a tu lote optimo");
                    }
                    break;
                }
            }
            articuloRepository.save(oca.getArt());
        }


    }

    public void crearporPeriodoFijo() {

       for ( Articulo art : articuloRepository.findAll()){

           if (art.getTipoLote() == TipoLote.PERIODOFIJO){

               // crear la OC



           }


        }

    }

    @Override
    @Transactional
    public List<OrdenCompra> buscarOrdenCompraPorEstado(String codigoEstado) {
        return ordenCompraRepository.findByEstadoCodigo(codigoEstado);
    }
}
