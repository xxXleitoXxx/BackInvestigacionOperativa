package org.example.services.clasesImp;

import org.example.dto.ArticuloDTO;
import org.example.dto.VentaArticuloDTO;
import org.example.dto.VentaDTO;
import org.example.enums.TipoLote;
import org.example.entity.*;
import org.example.repository.*;
import org.example.services.BaseService;
import org.example.services.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

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

    public boolean controlVenta (VentaDTO venta) throws Exception {
        Boolean posibilidad = true;
        for(VentaArticuloDTO va : venta.getVentaArticuloDTOS()) {
            int pedido = va.getCantArtVentDTO();
            int disponible;
            Articulo art1 = articuloRepository.findById(va.getArticuloDTO().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + va.getArticuloDTO().getId() ));
            disponible = art1.getStock();
            if (pedido > disponible) {
                posibilidad = false;
                break;

            }
        }
            return  posibilidad;
    }

    public void ActualizarStock(VentaDTO venta){
        for (VentaArticuloDTO va : venta.getVentaArticuloDTOS()) {
            Articulo art1 = articuloRepository.findById(va.getArticuloDTO().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + va.getArticuloDTO().getId() ));
            int stockRestante = art1.getStock() - va.getCantArtVentDTO();
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


    public void CrearOrdenCompra(VentaArticuloDTO va){
            Boolean ExisteOC = false;
            for (OrdenCompra ocv : ordenCompraRepository.findAll()){
                    if ((ocv.getEstadoOrdCom().getId() == 1 || ocv.getEstadoOrdCom().getId() == 2) && (ocv.getArticulo().getId() == va.getArticuloDTO().getId())) {
                        ExisteOC = true;
                        break;
                    }
            }
        Articulo art1 = articuloRepository.findById(va.getArticuloDTO().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + va.getArticuloDTO().getId() ));

        if ((art1.getStock() <= art1.getStockSeguridad()) && (ExisteOC == false)) {
                //generar orden de compra
            System.out.println("ghoasnbkj");
                OrdenCompra oc = new OrdenCompra();
                long idp = art1.getProveedorElegido().getId();
                Proveedor prov = proveedorRepository.findById(idp).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + idp));
                oc.setProv(art1.getProveedorElegido());
                LocalDateTime fechaactual =  LocalDateTime.now() ;
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
                LocalDateTime nuevaFecha = LocalDateTime.now();
                nuevaFecha.plusDays(demora);
                oc.setFechaLlegadaOrdCom(nuevaFecha);
                //buscar al crear los Estados en la base de datos
                long idoc = 1;
                EstadoOrdenCompra eoc = estadoOrdenCompraRepository.findById( idoc ).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + idoc));
                oc.setEstadoOrdCom(eoc);
                //OrdenCompraArticulo oca = new OrdenCompraArticulo(va.getArt().getStockSeguridad() - va.getArt().getStock(), va.getArt());
               oc.setCantPedida(loteoptimo - art1.getStock());
               oc.setMontoTotalOrdCom( ( loteoptimo - art1.getStock() ) * art1.getPrecioVenta() );
               oc.setArticulo(art1);
               ordenCompraRepository.save(oc);
            }



    }

    public List<VentaDTO> listarOC() {
        List<Venta> venta = ventaRepository.findAll();

        List<VentaDTO> ventaDTOS = new ArrayList<>();
        for (Venta ven : venta) {
            VentaDTO dto = ventaAdto(ven);
            ventaDTOS.add(dto);
        }

        return ventaDTOS;

    }

    public VentaDTO ventaAdto(Venta ven){
        VentaDTO dto = new VentaDTO();

        dto.setId(ven.getId());
        dto.setMontoTotalVentDTO(ven.getMontoTotalVent());
        dto.setFechaHoraVentDTO(ven.getFechaHoraVent());
        List<VentaArticuloDTO> lista = new ArrayList<>();
        for (VentaArticulo va : ven.getVentaArticulos()){
            VentaArticuloDTO vadto = new VentaArticuloDTO();
            vadto.setCantArtVentDTO(va.getCantArtVent());
            vadto.setMontoArtDTO(va.getMontoArt());
            ArticuloDTO artdto = new ArticuloDTO();
            artdto.setId(va.getArt().getId());
            vadto.setArticuloDTO(artdto);
            lista.add(vadto);
        }
        dto.setVentaArticuloDTOS(lista);


        return dto;
    }

    public Venta dtoAventa(VentaDTO dto) {

        Venta ven = new Venta();
        LocalDateTime fechaAcutaul = LocalDateTime.now();
        ven.setFechaHoraVent(fechaAcutaul);
        float montototal = 0;

        List<VentaArticulo> lista = new ArrayList<>();
        for (VentaArticuloDTO va : dto.getVentaArticuloDTOS()){

            Articulo art = articuloRepository.findById(va.getArticuloDTO().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + va.getArticuloDTO().getId() ));
            montototal = montototal + art.getPrecioVenta() * va.getCantArtVentDTO();
            VentaArticulo ventaArticulo = new VentaArticulo();
            float montoart= art.getPrecioVenta() * va.getCantArtVentDTO();
            ventaArticulo.setMontoArt(montoart);
            ventaArticulo.setArt(art);
            ventaArticulo.setCantArtVent(va.getCantArtVentDTO());
            lista.add(ventaArticulo);
        }


        ven.setVentaArticulos(lista);
        ven.setMontoTotalVent(montototal);

        return ven;

    }
}
