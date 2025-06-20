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

        //Validar código duplicado
        if (this.findByCodArt(articulo).isPresent()) {
            throw new Exception("Ya existe un artículo con el código: " + articulo.getCodArt());
        }
        //Guardar artículo
        return save(articulo);
    }

    //bajaArticulo
    @Transactional
    public Articulo bajaArticulo(Long id) throws Exception {

        //Traer artículo para darlo de baja.
        Articulo articuloDadoBaja = findById(id);

        //Comprobar que no haya unidades en Stock
        if(articuloDadoBaja.getStock() != 0){
            throw new Exception("El artículo aún tiene unidades en stock");
        }

        //Comprobar que no fue dado de baja anteriormente.
        if (articuloDadoBaja.getFechaHoraBajaArt() != null){
            throw new Exception("El artículo ya fue dado de baja");
        }

        //Comprobar que no haya una orden de compra pendiente con este artículo.
        if (comprobarOrdenCompraModificable(articuloDadoBaja)){
            throw new Exception("No se puede modificar el artículo porque ya se encuentra la Orden Pendiente o Enviada");
        }

        //Setear la fecha de baja como la actual.
        articuloDadoBaja.setFechaHoraBajaArt(LocalDateTime.now());
        //Dar de baja al artículo.
        this.update(id, articuloDadoBaja);

        return articuloDadoBaja;
    }

    //modificarArticulo
    @Transactional
    public Articulo modificarArticulo(Articulo articulo, Long id) throws Exception {

        // Traer artículo existente
        Articulo articuloExistente = this.findById(id);

        // Comprobar que no haya unidades en Stock
        if (articuloExistente.getStock() != 0) {
            throw new Exception("El artículo aún tiene unidades en stock");
        }

        // Comprobar que no fue dado de baja anteriormente
        if (articuloExistente.getFechaHoraBajaArt() != null) {
            throw new Exception("El artículo ya fue dado de baja");
        }

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
        if (articulo.getPrecioVenta() != null && articulo.getPrecioVenta() > 0) {
            articuloExistente.setPrecioVenta(articulo.getPrecioVenta());
        }

        // Guardar cambios
        return this.update(id, articuloExistente);
    }

    //listarArticulosActivos (sólo los no dados de baja)
    @Transactional
    public List<Articulo> listarArticulosActivos (){

        return articuloRepository.findByFechaHoraBajaArtIsNull();

    }

    //listarArticulosDadosDeBaja
    @Transactional
    public List<Articulo> listarArticulosDadosDeBaja (){

        return articuloRepository.findByFechaHoraBajaArtIsNotNull();

    }





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
