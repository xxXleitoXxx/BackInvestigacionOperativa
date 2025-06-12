package org.example.services.clasesImp;

import org.example.TipoLote;
import org.example.entity.*;
import org.example.repository.*;
import org.example.services.BaseService;
import org.example.services.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VentaServiceImp extends BaseServiceImpl <Venta,Long> implements BaseService <Venta,Long> {

    //Repositorio
    @Autowired
    VentaRepository ventaRepository;
    @Autowired
    ArticuloRepository articuloRepository;
    @Autowired
    ProveedorRepository proveedorRepository;
    @Autowired
    EstadoOrdenCompraRepository estadoOrdenCompraRepository;
    @Autowired
    OrdenCompraRepository ordenCompraRepository;

    //Constructor
    public VentaServiceImp(BaseRepository<Venta, Long> baseRespository, VentaRepository ventaRepository) {
        super(baseRespository);
        this.ventaRepository = ventaRepository;
    }

    public boolean controlVenta( Venta venta){
        Boolean posibilidad = true;
        for(VentaArticulo va : venta.getVentaArticulos()) {
            int pedido = va.getCantArtVent();
            int disponible = va.getArt().getStock();
            if (pedido > disponible) {
                posibilidad = false;
                break;
            }
        }
            return  posibilidad;
    }

    public void ActualizarStock(Venta venta){
        for (VentaArticulo va : venta.getVentaArticulos()) {
            int stockRestante = va.getArt().getStock() - va.getCantArtVent();
            va.getArt().setStock(stockRestante);
            articuloRepository.save(va.getArt());
            if (va.getArt().getTipoLote() == TipoLote.PEDIDOFIJO){
                Boolean ExisteOC = false;
                ordenCompraRepository.findAll();
                for (OrdenCompra ocv : ordenCompraRepository.findAll()){
                    for (OrdenCompraArticulo ocav : ocv.getOrdenCompraArticulo()){
                        if ((ocv.getEstadoOrdCom().getNomEOC() == "Pendiente" || ocv.getEstadoOrdCom().getNomEOC() == "Enviada") && (ocav.getArt() == va.getArt())) {
                            ExisteOC = true;
                            break;
                        }
                    }
                }
                if ((va.getArt().getStock() >= va.getArt().getSotckSeguridad()) && (ExisteOC == false)) {

                    //generar orden de compra
                    OrdenCompra oc = new OrdenCompra();
                    oc.setProv(va.getArt().getProvedorElegido());
                    Date fechaactual = new Date() ;
                    oc.setFechaPedidoOrdCom(fechaactual);
                     long idp = va.getArt().getProvedorElegido().getId();
                    Proveedor prov = proveedorRepository.findById(idp).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + idp));
                    int demora = 1 ;
                    for (ProveedorArticulo pa : prov.getProveedorArticulos()){
                        if (pa.getArt() == va.getArt()){
                            demora = pa.getDemoraEntrega();
                            break;
                        }
                    }
                    Date nuevaFecha = new Date(fechaactual.getTime() + ( demora * 86400000  ));
                    oc.setFechaLlegadaOrdCom(nuevaFecha);
                    //buscar al crear los Estados en la base de datos
                    long idoc = 2;
                    EstadoOrdenCompra eoc = estadoOrdenCompraRepository.findById( idoc ).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + idoc));
                    oc.setEstadoOrdCom(eoc);
                    OrdenCompraArticulo oca = new OrdenCompraArticulo(va.getArt().getSotckSeguridad() - va.getArt().getStock(), va.getArt());
                    List<OrdenCompraArticulo> loca = new ArrayList<>();
                    loca.add(oca);
                    oc.setOrdenCompraArticulo(loca);
                    ordenCompraRepository.save(oc);
                }

            }
        }
    }

}
