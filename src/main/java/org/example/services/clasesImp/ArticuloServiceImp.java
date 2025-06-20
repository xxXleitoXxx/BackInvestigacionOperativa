package org.example.services.clasesImp;

import jakarta.transaction.Transactional;
import org.example.entity.Articulo;
import org.example.entity.OrdenCompra;
import org.example.entity.OrdenCompraArticulo;
import org.example.repository.ArticuloRepository;
import org.example.repository.BaseRepository;
import org.example.services.BaseServiceImpl;
import org.example.services.interfaces.ArticuloService;
import org.example.services.interfaces.OrdenCompraService;
import org.example.services.interfaces.ProveedorService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ArticuloServiceImp extends BaseServiceImpl<Articulo,Long> implements ArticuloService {

    //Repositorio.
    private final ArticuloRepository articuloRepository;

    //Servicios consumidos.
    private final   ProveedorService proveedorService;
    private final  OrdenCompraService ordenCompraService;

    public ArticuloServiceImp(BaseRepository<Articulo, Long> baseRespository, ArticuloRepository articuloRepository, ProveedorService proveedorService, OrdenCompraService ordenCompraService) {
        super(baseRespository);
        this.articuloRepository = articuloRepository;
        this.proveedorService = proveedorService;
        this.ordenCompraService =ordenCompraService;
    }

    //Métodos

    //findByCodArt
    @Transactional
    public Optional<Articulo> findByCodArt (Articulo articulo) {
            return articuloRepository.findByCodArt(articulo.getCodArt());
    }

    //alta Artículo
    @Transactional
    public Articulo altaArticulo(Articulo articulo) throws Exception {

        // Validar código duplicado
        if (this.findByCodArt(articulo).isPresent()) {
            throw new Exception("Ya existe un artículo con el código: " + articulo.getCodArt());
        }
        // Validar existencia del proveedor
//        if (proveedorService.findById(articulo.getProveedorElegido().getId()) == null) {
//            throw new Exception("El proveedor con ID " + articulo.getProveedorElegido().getId() + " no existe.");
//        }
        // Guardar artículo
        return save(articulo);
    }

    //bajaArticulo
    @Transactional
    public Articulo bajaArticulo(Articulo articulo) throws Exception {

        //Validar que el articulo existe
        if (this.findById(articulo.getId()) == null){
            throw new Exception("El Artículo ingresado no existe");
        }

        //Comprobar que no haya unidades en Stock
        if(this.findById(articulo.getId()).getStock() != 0){
            throw new Exception("El artículo aún tiene unidades en stock");
        }

        //Comprobar que no fue dado de baja anteriormente.
        if (this.findById(articulo.getId()).getFechaHoraBajaArt() != null){
            throw new Exception("El artículo ya fue dado de baja");
        }

        //Comprobar que no haya una orden de compra pendiente con este artículo.
        if (comprobarOrdenCompraModificable(articulo)){
            throw new Exception("No se puede modificar el artículo porque ya se encuentra la Orden Pendiente o Enviada");
        }

        //Setear la fecha de baja como la actual.
        articulo.setFechaHoraBajaArt(LocalDateTime.now());
        //dar de baja al artículo.
        this.update(articulo.getId(), articulo);

        return articulo;
    }

    //modificarArticulo
    @Transactional
    public Articulo modificarArticulo (Articulo articulo) throws Exception {

        //Validar que el articulo existe
        if (this.findById(articulo.getId()) == null){
            throw new Exception("El Artículo ingresado no existe");
        }

        //Comprobar que no haya unidades en Stock
        if(this.findById(articulo.getId()).getStock() != 0){
            throw new Exception("El artículo aún tiene unidades en stock");
        }

        //Comprobar que no fue dado de baja anteriormente.
        if (this.findById(articulo.getId()).getFechaHoraBajaArt() != null){
            throw new Exception("El artículo ya fue dado de baja");
        }

        Articulo articuloExistente = this.findById(articulo.getId());

        // Actualizar campos si vienen válidos
        if (articulo.getCodArt() != null && !articulo.getCodArt().isBlank()) {
            articuloExistente.setCodArt(articulo.getCodArt());
        }

        if (articulo.getNomArt() != null && !articulo.getNomArt().isBlank()) {
            articuloExistente.setNomArt(articulo.getNomArt());
        }

        if (articulo.getDescripcionArt() != null && !articulo.getDescripcionArt().isBlank()) {
            articuloExistente.setDescripcionArt(articulo.getDescripcionArt());
        }

        if (articulo.getPrecioVenta() > 0) {
            articuloExistente.setPrecioVenta(articulo.getPrecioVenta());
        }
        return this.update(articuloExistente.getId(), articuloExistente);
    }

    //Métodos auxiliares.

    //comprobarOrdenCompra Pendiente (No verifica que el artículo exista)
    @Transactional
    private Boolean comprobarOrdenCompraModificable(Articulo articulo) {

        // Traer las órdenes de compra pendientes y enviadas
        List<OrdenCompra> ordenesPendientes = ordenCompraService.buscarOrdenCompraPorEstado("Pendiente");
        List<OrdenCompra> ordenesEnviadas = ordenCompraService.buscarOrdenCompraPorEstado("Enviado");

        // Unir listas
        List<OrdenCompra> ordenesNoModificables = new ArrayList<>();
        ordenesNoModificables.addAll(ordenesPendientes);
        ordenesNoModificables.addAll(ordenesEnviadas);

        // Iterar sobre cada orden y sus artículos asociados
        for (OrdenCompra orden : ordenesNoModificables) {
            for (OrdenCompraArticulo oca : orden.getOrdenCompraArticulo()) {
                if (oca.getArt().getId().equals(articulo.getId())) {
                    return true; // El artículo está en una orden no modificable (pendiente o enviada)
                }
            }
        }
        return false; // El artículo no está en ninguna orden pendiente ni enviada
    }

}
