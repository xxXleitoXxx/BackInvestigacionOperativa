package org.example.services.clasesImp;

import org.example.enums.TipoLote;
import org.example.entity.*;
import org.example.repository.*;
import org.example.services.BaseService;
import org.example.services.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

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
            int disponible;
            Articulo art1 = articuloRepository.findById(va.getArt().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + va.getArt().getId() ));
            disponible = art1.getStock();
            if (pedido >= disponible) {
                posibilidad = false;
                break;
            }
        }
            return  posibilidad;
    }

    public void ActualizarStock(Venta venta){
        for (VentaArticulo va : venta.getVentaArticulos()) {
            Articulo art1 = articuloRepository.findById(va.getArt().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + va.getArt().getId() ));
            int stockRestante = art1.getStock() - va.getCantArtVent();
            art1.setStock(stockRestante);
            articuloRepository.save(art1);
            Proveedor prov = proveedorRepository.findById(art1.getProveedorElegido().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + art1.getProveedorElegido().getId() ));
            for (ProveedorArticulo pa : prov.getProveedorArticulos()){
                if ( (pa.getArt() == art1) && (pa.getTipoLote() == TipoLote.LOTEFIJO)   ){
                    System.out.println("se genero oc");
                    CrearOrdenCompra(va);
                }
            }
        }
    }


    public void CrearOrdenCompra(VentaArticulo va){
            Boolean ExisteOC = false;
            for (OrdenCompra ocv : ordenCompraRepository.findAll()){
                    if ((ocv.getEstadoOrdCom().getId() == 1 || ocv.getEstadoOrdCom().getId() == 2) && (ocv.getArticulo().getId() == va.getArt().getId())) {
                        ExisteOC = true;
                        break;
                    }
            }
            if ((va.getArt().getStock() >= va.getArt().getStockSeguridad()) && (ExisteOC == false)) {
                //generar orden de compra
                OrdenCompra oc = new OrdenCompra();
                long ida = va.getArt().getId();
                Articulo art1 = articuloRepository.findById(ida).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + ida));
                long idp = art1.getProveedorElegido().getId();
                Proveedor prov = proveedorRepository.findById(idp).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + idp));
                oc.setProv(art1.getProveedorElegido());
                Date fechaactual = new Date() ;
                oc.setFechaPedidoOrdCom(fechaactual);
                int demora = 1 ;
                int loteoptimo = 0;
                for (ProveedorArticulo pa : prov.getProveedorArticulos()){
                    if (pa.getArt() == art1){
                        demora = pa.getDemoraEntrega();
                        loteoptimo = pa.getLoteOptimo();
                        break;
                    }
                }
                Date nuevaFecha = new Date(fechaactual.getTime() + ( demora * 86400000  ));
                oc.setFechaLlegadaOrdCom(nuevaFecha);
                //buscar al crear los Estados en la base de datos
                long idoc = 1;
                EstadoOrdenCompra eoc = estadoOrdenCompraRepository.findById( idoc ).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + idoc));
                oc.setEstadoOrdCom(eoc);
                //OrdenCompraArticulo oca = new OrdenCompraArticulo(va.getArt().getStockSeguridad() - va.getArt().getStock(), va.getArt());
               oc.setCantPedida(loteoptimo - art1.getStock());
               oc.setMontoTotalOrdCom( ( loteoptimo - art1.getStock() ) * art1.getPrecioVenta() );
               ordenCompraRepository.save(oc);
            }



    }

}
