package org.example.services.clasesImp;
import jakarta.transaction.Transactional;
import org.example.dto.ArticuloDTO;
import org.example.dto.ProveedorDTO;
import org.example.entity.Articulo;
import org.example.entity.OrdenCompra;
import org.example.entity.OrdenCompraArticulo;
import org.example.entity.Proveedor;
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
    public ArticuloDTO altaArticulo(ArticuloDTO articuloDTO) throws Exception {

        Articulo articuloNuevo = new Articulo();
        articuloNuevo.setCodArt(articuloDTO.getCodArt());
        articuloNuevo.setNomArt(articuloDTO.getNomArt());
        articuloNuevo.setPrecioVenta(articuloDTO.getPrecioVenta());
        articuloNuevo.setDescripcionArt(articuloDTO.getDescripcionArt());
        articuloNuevo.setStock(articuloDTO.getStock());
        articuloNuevo.setDemandaDiaria(articuloDTO.getDemandaDiaria());
        articuloNuevo.setDesviacionEstandarUsoPeriodoEntrega(articuloDTO.getDesviacionEstandarUsoPeriodoEntrega());
        articuloNuevo.setDesviacionEstandarDurantePeriodoRevisionEntrega(articuloDTO.getDesviacionEstandarDurantePeriodoRevisionEntrega());

        //Validar código duplicado
        if (this.findByCodArt(articuloNuevo).isPresent()) {
            throw new Exception("Ya existe un artículo con el código: " + articuloDTO.getCodArt());
        }

        // Si querés asignar proveedor:
        if (articuloDTO.getProveedorDTO() != null && articuloDTO.getProveedorDTO().getId() != null) {

            Proveedor proveedor = proveedorService.findById(articuloDTO.getProveedorDTO().getId());
            if (proveedor == null){throw new Exception("El proveedor no existe");}

            articuloNuevo.setProveedorElegido(proveedor);

        } else {
            articuloNuevo.setProveedorElegido(null); // explícitamente null

        }

        //Guardar artículo
        Articulo articuloGuardado = articuloRepository.save(articuloNuevo);

        return crearArticuloDTO(articuloNuevo);
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

    //listaProveedorPorArticulo
    @Transactional
    public List<Proveedor> listarProveedoresPorArticulo (Long id) throws Exception{

        //Buscar artículo
        Articulo articulo = this.findById(id);
        //Buscar lista de proveedores activos por artículo.
        return proveedorService.findProveedoresActivosByArticuloId(id);

    }

    //listarArticulosFaltantes
    @Transactional
    public List<Articulo> listarArticulosFaltantes(){

        return articuloRepository.findArticulosFaltantes();

    }

    //Métodos auxiliares.

    //crearArticuloDTO
    private ArticuloDTO crearArticuloDTO(Articulo articulo) {
        ArticuloDTO dto = new ArticuloDTO();

        dto.setId(articulo.getId());
        dto.setCodArt(articulo.getCodArt());
        dto.setNomArt(articulo.getNomArt());
        dto.setPrecioVenta(articulo.getPrecioVenta());
        dto.setDescripcionArt(articulo.getDescripcionArt());
        dto.setFechaHoraBajaArt(articulo.getFechaHoraBajaArt());
        dto.setStock(articulo.getStock());
        dto.setStockSeguridad(articulo.getStockSeguridad());
        dto.setDemandaDiaria(articulo.getDemandaDiaria());
        dto.setDesviacionEstandarUsoPeriodoEntrega(articulo.getDesviacionEstandarUsoPeriodoEntrega());
        dto.setDesviacionEstandarDurantePeriodoRevisionEntrega(articulo.getDesviacionEstandarDurantePeriodoRevisionEntrega());

        if (articulo.getProveedorElegido() != null) {
            ProveedorDTO proveedorDTO = new ProveedorDTO();
            proveedorDTO.setId(articulo.getProveedorElegido().getId());

            dto.setProveedorDTO(proveedorDTO);
        }

        return dto;
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
