package org.example.services.clasesImp;

import jakarta.transaction.Transactional;
import org.example.dto.*;
import org.example.enums.TipoLote;
import org.example.entity.*;
import org.example.repository.*;
import org.example.services.BaseServiceImpl;
import org.example.services.interfaces.OrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdenCompraServiceImp extends BaseServiceImpl<OrdenCompra,Long> implements OrdenCompraService {

    @Autowired
    OrdenCompraRepository ordenCompraRepository;
    @Autowired
    ArticuloRepository articuloRepository;
    @Autowired
    ProveedorRepository proveedorRepository;
    @Autowired
    EstadoOrdenCompraRepository estadoOrdenCompraRepository;
    @Autowired
    ProveedorArticuloRepository proveedorArticuloRepository;


    public OrdenCompraServiceImp(BaseRepository<OrdenCompra, Long> baseRespository, OrdenCompraRepository ordenCompraRepository) {
        super(baseRespository);
        this.ordenCompraRepository = ordenCompraRepository;
    }

    public Boolean mod(OrdenCompraDTO ordenCompra) {
        boolean posible = false;
        OrdenCompra oc = ordenCompraRepository.findById(ordenCompra.getId()).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + ordenCompra.getId()));
        if (oc.getEstadoOrdCom().getNomEOC() == "Pendiente"){
            posible = true;
        }
        return posible;
    }

    public Boolean crear(OrdenCompraDTO ordenCompra) {
        boolean PuedoCrear = true;
        System.out.println("A");
        if (ordenCompra.getEstadoOrdenCompraDTO().getNomEOC() != "Pendiente"){
            System.out.println("B");
           PuedoCrear = false;
        }
        System.out.println("C");
        Articulo art = articuloRepository.findById(ordenCompra.getArticuloDTO().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + ordenCompra.getArticuloDTO().getId() ));
        System.out.println("D");
        //ordenCompra.getArticuloDTO();
        for (OrdenCompra oc : ordenCompraRepository.findAll()) {
            System.out.println("E");
            if ((ordenCompra.getArticuloDTO().getId() == art.getId()) && (oc.getEstadoOrdCom().getNomEOC() != "Finalizada")) {
                        PuedoCrear = false;
                System.out.println("F");
                        break;
            }
            break;
        }
        System.out.println("G");
        Proveedor prov = proveedorRepository.findById(art.getProveedorElegido().getId()).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + art.getProveedorElegido().getId() ));
        System.out.println("H");
        for (ProveedorArticulo pa : prov.getProveedorArticulos()){
            System.out.println("I");
            if ( prov.getId() == ordenCompra.getProveedorDTO().getId() ){
                PuedoCrear = true;
                System.out.println("J");
            }
        }
        System.out.println("K");
        return PuedoCrear;
    }

    public Boolean cancelar(OrdenCompraDTO ordenCompra) {
        Boolean posible = true;
        OrdenCompra oc = ordenCompraRepository.findById(ordenCompra.getId()).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + ordenCompra.getId()));
        if (oc.getEstadoOrdCom().getId() != 1){
            posible = false;
        }
        return posible;
    }

    public Boolean finalizar(OrdenCompraDTO ordenCompra) {
        Boolean posible = false;
        OrdenCompra oc = ordenCompraRepository.findById(ordenCompra.getId()).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + ordenCompra.getId()));
        if (oc.getEstadoOrdCom().getId() == 2){
            posible = true;
        }
        return posible;
    }

    public void actualizarStock(OrdenCompraDTO ordenCompra) {
        Articulo articulo = articuloRepository.findById(ordenCompra.getArticuloDTO().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + ordenCompra.getArticuloDTO().getId()));
        int StockNuevo = articulo.getStock() + ordenCompra.getCantPedida();
        articulo.setStock(StockNuevo);
            Proveedor prov = proveedorRepository.findById(ordenCompra.getProveedorDTO().getId()).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + ordenCompra.getProveedorDTO().getId()));
            for (ProveedorArticulo pa : prov.getProveedorArticulos()){
                if ( pa.getArt().getId() ==  ordenCompra.getArticuloDTO().getId() ){
                    if(StockNuevo < pa.getLoteOptimo()){
                        ResponseEntity.status(HttpStatus.OK).body("No llegaste a tu lote optimo");
                    }
                    break;
                }
            }
            articuloRepository.save(articulo);
    }

    @Transactional
    public void crearporPeriodoFijo() {
        LocalDateTime fecahActual = LocalDateTime.now();
        for ( Articulo art : articuloRepository.findAll()) {
            if (art.getDiaDePedido() != null){
                System.out.println(art.getNomArt());

            Proveedor prov = proveedorRepository.findById(art.getProveedorElegido().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + art.getProveedorElegido().getId()));
            for (ProveedorArticulo pa : prov.getProveedorArticulos()) {
                if ((pa.getArt().getId().equals(art.getId()) && (pa.tipoLote == TipoLote.PERIODOFIJO) && (art.getDiaDePedido().getDayOfYear() == fecahActual.getDayOfYear()))) {
                    Boolean ExisteOC = false;
                    for (OrdenCompra ocv : ordenCompraRepository.findAll()) {
                        if ((ocv.getEstadoOrdCom().getId() == 1 || ocv.getEstadoOrdCom().getId() == 2) && (ocv.getArticulo().getId() == art.getId())) {
                            ExisteOC = true;
                            System.out.println("1");
                            break;
                        }
                    }
                    if ((art.getStock() <= pa.getLoteOptimo()) && (ExisteOC == false)) {
                        //generar orden de compra
                        System.out.println("2");
                        OrdenCompra oc = new OrdenCompra();
                        oc.setProv(art.getProveedorElegido());
                        LocalDateTime fechaactual = LocalDateTime.now();
                        oc.setFechaPedidoOrdCom(fechaactual);
                        int demora = 1;
                        int loteoptimo = 0;
                        float precio = 0;

                        for (ProveedorArticulo pa1 : prov.getProveedorArticulos()) {
                            if (pa1.getArt().getId() == art.getId()) {
                                demora = pa.getDemoraEntrega();
                                loteoptimo = pa.getLoteOptimo();
                                precio = pa.getCostoUnitario();
                            }
                            LocalDateTime nuevaFecha = LocalDateTime.now();
                            oc.setFechaLlegadaOrdCom(LocalDateTime.now().plusDays(demora));
                            //buscar al crear los Estados en la base de datos
                            long idoc = 1;
                            EstadoOrdenCompra eoc = estadoOrdenCompraRepository.findById(idoc).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + idoc));
                            oc.setEstadoOrdCom(eoc);
                            oc.setCantPedida(loteoptimo - art.getStock());
                            oc.setMontoTotalOrdCom((loteoptimo - art.getStock()) * precio);
                            break;
                        }
                            oc.setArticulo(art);
                            ordenCompraRepository.save(oc);


                    }
                    art.setDiaDePedido(fecahActual.plusDays(pa.getDemoraEntrega()));
                    articuloRepository.save(art);
                }
                }
            }
        }
    }

    @Override
    public List<OrdenCompra> buscarOrdenCompraPorEstado(String codigoEstado) {
        return ordenCompraRepository.findByEstadoCodigo(codigoEstado);
    }

    public OrdenCompra DTOaOCNEW (OrdenCompraDTO dto ){

        OrdenCompra orden = new OrdenCompra();

        orden.setCantPedida(dto.getCantPedida());
        LocalDateTime fechaactual =  LocalDateTime.now() ;
        orden.setFechaPedidoOrdCom(fechaactual);
        Articulo art = articuloRepository.findById(dto.getArticuloDTO().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + dto.getArticuloDTO().getId() ));
        orden.setArticulo(art);
        EstadoOrdenCompra eoc = estadoOrdenCompraRepository.findById(dto.getEstadoOrdenCompraDTO().getId()).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + dto.getEstadoOrdenCompraDTO().getId() ));
        orden.setEstadoOrdCom(eoc);
        Proveedor proveedor = proveedorRepository.findById(art.getProveedorElegido().getId()).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + art.getProveedorElegido().getId() ));
        orden.setProv(proveedor);
        int demora = 0;
        float precio = 0;
        for (ProveedorArticulo pa : proveedorArticuloRepository.findAll()){
            if (pa.getArt() == art){
                demora = pa.getDemoraEntrega();
                precio = pa.getCostoUnitario();
            }
        }
        orden.setFechaLlegadaOrdCom(fechaactual.plusDays(demora));
        orden.setMontoTotalOrdCom(dto.getCantPedida() * precio);
        return orden;




    }

    public OrdenCompra DTOaOC (OrdenCompraDTO dto ){

        OrdenCompra orden = ordenCompraRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("OrdenCompra no encontrado con ID: " + dto.getId() ));
        orden.setCantPedida(dto.getCantPedida());
        Articulo art = articuloRepository.findById(dto.getArticuloDTO().getId()).orElseThrow(() -> new RuntimeException("Articulo no encontrado con ID: " + dto.getArticuloDTO().getId() ));
        EstadoOrdenCompra eoc = estadoOrdenCompraRepository.findById(dto.getEstadoOrdenCompraDTO().getId()).orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + dto.getEstadoOrdenCompraDTO().getId() ));
        orden.setEstadoOrdCom(eoc);
        Proveedor proveedor = proveedorRepository.findById(art.getProveedorElegido().getId()).orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + art.getProveedorElegido().getId() ));
        orden.setProv(proveedor);
        long demora = 1;
        float precio = 0;
        for (ProveedorArticulo pa : proveedorArticuloRepository.findAll()){
            if (pa.getArt() == art){
                demora = pa.getDemoraEntrega();
                precio = pa.getCostoUnitario();
            }
        }
        orden.setMontoTotalOrdCom(dto.getCantPedida() * precio);
        if (eoc.getNomEOC() == "Enviada") {
            LocalDateTime fechaactual = LocalDateTime.now();
            LocalDateTime nuevaFecha = LocalDateTime.now();
            fechaactual.plusDays(demora);
            orden.setFechaLlegadaOrdCom(nuevaFecha);
        }
        return orden;




    }


    public List<OrdenCompraDTO> listarOC() {

        List<OrdenCompra> ordenCompras = ordenCompraRepository.findAll();

        List<OrdenCompraDTO> ordenCompraDTOS = new ArrayList<>();

        for (OrdenCompra oc : ordenCompras) {
            OrdenCompraDTO dto = ocadto(oc);
            ordenCompraDTOS.add(dto);
        }

        return ordenCompraDTOS;
    }

    public OrdenCompraDTO ocadto (OrdenCompra oc){

        OrdenCompraDTO dto = new OrdenCompraDTO();

        dto.setId(oc.getId());
        dto.setCantPedida(oc.getCantPedida());
        dto.setFecha(oc.getFechaPedidoOrdCom().toString());
        dto.setMontoTotal(oc.getMontoTotalOrdCom());
        dto.setFechaLlegadaOrdCom(oc.getFechaLlegadaOrdCom());
        dto.setFechaPedidoOrdCom(oc.getFechaPedidoOrdCom());

        ArticuloOCDTO articuloDTO = new ArticuloOCDTO();
        articuloDTO.setId(oc.getArticulo().getId());
        articuloDTO.setNomArt(oc.getArticulo().getNomArt());
        dto.setArticuloDTO(articuloDTO);

        EstadoOrdenCompraDTO estadoOrdenCompraDTO = new EstadoOrdenCompraDTO();
        estadoOrdenCompraDTO.setId(oc.getEstadoOrdCom().getId());
        estadoOrdenCompraDTO.setNomEOC(oc.getEstadoOrdCom().getNomEOC());
        dto.setEstadoOrdenCompraDTO(estadoOrdenCompraDTO);

        ProveedorOCDTO proveedorOCDTO = new ProveedorOCDTO();
        proveedorOCDTO.setId(oc.getProv().getId());
        proveedorOCDTO.setNomProv(oc.getProv().getNomProv());
        dto.setProveedorDTO(proveedorOCDTO);

        return dto;
    }
}
