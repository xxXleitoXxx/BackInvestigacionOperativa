package org.example.services.clasesImp;
import jakarta.transaction.Transactional;
import org.example.dto.ArticuloDTO;
import org.example.dto.ProveedorDTO;
import org.example.entity.Articulo;
import org.example.entity.OrdenCompra;
import org.example.entity.Proveedor;
import org.example.entity.ProveedorArticulo;
import org.example.enums.TipoLote;
import org.example.repository.ArticuloRepository;
import org.example.repository.BaseRepository;
import org.example.services.BaseServiceImpl;
import org.example.services.EstrategiaCalculoInventario.EstrategiaCalculoInventario;
import org.example.services.EstrategiaCalculoInventario.FabricaEstrategiaCalculoInventario;
import org.example.services.interfaces.ArticuloService;
import org.example.services.interfaces.ProveedorArticuloService;
import org.example.services.interfaces.OrdenCompraService;
import org.example.services.interfaces.ProveedorService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ArticuloServiceImp extends BaseServiceImpl<Articulo,Long> implements ArticuloService {

    // Repositorio.
    private final ArticuloRepository articuloRepository;

    // Servicios consumidos.
    private final ProveedorService proveedorService;
    private final OrdenCompraService ordenCompraService;
    private final ProveedorArticuloService proveedorArticuloService;
    private final FabricaEstrategiaCalculoInventario fabricaEstrategiaCalculoInventario;

    public ArticuloServiceImp(BaseRepository<Articulo, Long> baseRespository, ArticuloRepository articuloRepository, ProveedorService proveedorService, OrdenCompraService ordenCompraService, ProveedorArticuloService proveedorArticuloService, FabricaEstrategiaCalculoInventario fabricaEstrategiaCalculoInventario) {
        super(baseRespository);
        this.articuloRepository = articuloRepository;
        this.proveedorService = proveedorService;
        this.ordenCompraService = ordenCompraService;
        this.proveedorArticuloService = proveedorArticuloService;
        this.fabricaEstrategiaCalculoInventario = fabricaEstrategiaCalculoInventario;
    }


    //Métodos

    //alta Artículo
    @Transactional
    public ArticuloDTO altaArticulo(ArticuloDTO articuloDTO) throws Exception {

        //Validar código duplicado
        if (findByCodArt(articuloDTO.getCodArt()).isPresent()) {
            throw new Exception("Ya existe un artículo con el código: " + articuloDTO.getCodArt());
        }

        //Crear Articulo en memoria
        Articulo articuloNuevo = new Articulo();
        articuloNuevo.setCodArt(articuloDTO.getCodArt());
        articuloNuevo.setNomArt(articuloDTO.getNomArt());
        articuloNuevo.setPrecioVenta(articuloDTO.getPrecioVenta());
        articuloNuevo.setDescripcionArt(articuloDTO.getDescripcionArt());
        articuloNuevo.setStock(articuloDTO.getStock());
        articuloNuevo.setDemandaDiaria(articuloDTO.getDemandaDiaria());
        articuloNuevo.setDesviacionEstandarUsoPeriodoEntrega(articuloDTO.getDesviacionEstandarUsoPeriodoEntrega());
        articuloNuevo.setDesviacionEstandarDurantePeriodoRevisionEntrega(articuloDTO.getDesviacionEstandarDurantePeriodoRevisionEntrega());

        // Si querés asignar proveedor:
        if (articuloDTO.getProveedorDTO() != null && articuloDTO.getProveedorDTO().getId() != null) {

            Proveedor proveedor = proveedorService.findById(articuloDTO.getProveedorDTO().getId());
            if (proveedor == null) {
                throw new Exception("El proveedor no existe");
            }

            articuloNuevo.setProveedorElegido(proveedor);

        } else {
            articuloNuevo.setProveedorElegido(null);

        }

        //Guardar artículo
        Articulo articuloGuardado = save(articuloNuevo);

        return crearArticuloDTO(articuloGuardado);
    }

    @Transactional
    public ArticuloDTO bajaArticulo(ArticuloDTO articuloDTO) throws Exception {
        // Buscar el artículo una sola vez
        Articulo articulo = findById(articuloDTO.getId());

        if (articulo == null) {
            throw new Exception("El articulo no existe");
        }

        if (comprobarStockAgotado(articuloDTO.getId())) {
            throw new Exception("No se puede modificar, stock no agotado");
        }

        if (articulo.getFechaHoraBajaArt() != null) {
            throw new Exception("El artículo ya fue dado de baja");
        }

        if (comprobarOrdenDeCompraPendienteOEnviada(articuloDTO.getId())) {
            throw new Exception("No se puede modificar el artículo porque ya se encuentra la Orden Pendiente o Enviada");
        }

        articulo.setFechaHoraBajaArt(LocalDateTime.now());
        update(articulo.getId(), articulo);

        return crearArticuloDTO(articulo);
    }

    //modifcarArticulo
    @Transactional
    public ArticuloDTO modificarArticulo(ArticuloDTO articuloDTO) throws Exception {

        // Validar que venga el ID en el DTO
        if (articuloDTO.getId() == null) {
            throw new Exception("El ID del artículo es obligatorio para modificar.");
        }

        // Traer artículo existente
        Articulo articuloExistente = this.findById(articuloDTO.getId());

        // Comprobar que no haya unidades en stock
        if (articuloExistente.getStock() != 0) {
            throw new Exception("El artículo aún tiene unidades en stock");
        }

        // Comprobar que no fue dado de baja anteriormente
        if (articuloExistente.getFechaHoraBajaArt() != null) {
            throw new Exception("El artículo ya fue dado de baja");
        }

        // Comprobar que no tenga órdenes de compra pendientes o enviadas
        if (comprobarOrdenDeCompraPendienteOEnviada(articuloDTO.getId())) {
            throw new Exception("El artículo tiene órdenes de compra pendientes o enviadas y no puede ser modificado");
        }

        // === Actualizar campos básicos ===
        if (articuloDTO.getCodArt() != null && !articuloDTO.getCodArt().isBlank()) {
            articuloExistente.setCodArt(articuloDTO.getCodArt());
        }

        if (articuloDTO.getNomArt() != null && !articuloDTO.getNomArt().isBlank()) {
            articuloExistente.setNomArt(articuloDTO.getNomArt());
        }

        if (articuloDTO.getPrecioVenta() != null && articuloDTO.getPrecioVenta() > 0) {
            articuloExistente.setPrecioVenta(articuloDTO.getPrecioVenta());
        }

        // === Verificar y aplicar cambios en demanda y desviaciones ===
        Integer nuevaDemandaDiaria = articuloDTO.getDemandaDiaria();
        Integer nuevaDesvEstandarUso = articuloDTO.getDesviacionEstandarUsoPeriodoEntrega();
        Integer nuevaDesvEstandarRevision = articuloDTO.getDesviacionEstandarDurantePeriodoRevisionEntrega();

        boolean cambioDemanda = nuevaDemandaDiaria != null && !nuevaDemandaDiaria.equals(articuloExistente.getDemandaDiaria());
        boolean cambioDesvUso = nuevaDesvEstandarUso != null && !nuevaDesvEstandarUso.equals(articuloExistente.getDesviacionEstandarUsoPeriodoEntrega());
        boolean cambioDesvRevision = nuevaDesvEstandarRevision != null && !nuevaDesvEstandarRevision.equals(articuloExistente.getDesviacionEstandarDurantePeriodoRevisionEntrega());

        if (cambioDemanda || cambioDesvUso || cambioDesvRevision) {

            // Validar que los tres valores estén cargados y sean válidos
            if (nuevaDemandaDiaria == null || nuevaDemandaDiaria <= 0 ||
                    nuevaDesvEstandarUso == null || nuevaDesvEstandarUso <= 0 ||
                    nuevaDesvEstandarRevision == null || nuevaDesvEstandarRevision <= 0) {
                throw new Exception("Los valores de demanda diaria y desviaciones deben ser mayores a cero si alguno cambia.");
            }

            // Setear todos los valores
            articuloExistente.setDemandaDiaria(nuevaDemandaDiaria);
            articuloExistente.setDesviacionEstandarUsoPeriodoEntrega(nuevaDesvEstandarUso);
            articuloExistente.setDesviacionEstandarDurantePeriodoRevisionEntrega(nuevaDesvEstandarRevision);

            // Recalcular si hay relación activa con proveedor elegido
            Proveedor proveedorElegido = articuloExistente.getProveedorElegido(); //Arreglar esto deber ser sobre cualquier proveedor activo relacionado al articulo
            if (proveedorElegido != null) {
                Optional<ProveedorArticulo> provArtOpt = obtenerProveedorArticuloRelacionado(articuloExistente.getProveedorElegido().getProveedorArticulos(),articuloExistente);
                if (provArtOpt.isPresent()) {
                    ProveedorArticulo proveedorArticulo = provArtOpt.get();

                    EstrategiaCalculoInventario estrategia = fabricaEstrategiaCalculoInventario.obtener(proveedorArticulo.getTipoLote());

                    ProveedorArticulo proveedorArticuloCalculado = estrategia.calcular(proveedorArticulo);

                    proveedorArticuloService.update(proveedorArticuloCalculado.getId(), proveedorArticuloCalculado);
                    update(articuloDTO.getId(), proveedorArticuloCalculado.getArt());
                }
            }
        }

        // Guardar cambios y retornar DTO
        Articulo articuloActualizado = update(articuloDTO.getId(), articuloExistente);
        return crearArticuloDTO(articuloActualizado);
    }


    @Transactional
    public ArticuloDTO modificarParametrosInventario(ArticuloDTO articuloDTO) throws Exception {

        // Validar ID
        if (articuloDTO.getId() == null) {
            throw new Exception("El ID del artículo es obligatorio.");
        }

        // Traer artículo existente
        Articulo articulo = this.findById(articuloDTO.getId());

        // Setear nuevos valores
        articulo.setDemandaDiaria(articuloDTO.getDemandaDiaria());
        articulo.setDesviacionEstandarUsoPeriodoEntrega(articuloDTO.getDesviacionEstandarUsoPeriodoEntrega());
        articulo.setDesviacionEstandarDurantePeriodoRevisionEntrega(articuloDTO.getDesviacionEstandarDurantePeriodoRevisionEntrega());

        // Guardar y devolver DTO
        Articulo articuloActualizado = this.update(articulo.getId(), articulo);
        return crearArticuloDTO(articuloActualizado);
    }


    //listarArticulosActivos (sólo los no dados de baja)
    @Transactional
    public List<ArticuloDTO> listarArticulosActivos() {

        // Obtener lista de Articulo que están activos (fechaHoraBajaArt = null)
        List<Articulo> listaArticulosActivos = articuloRepository.findByFechaHoraBajaArtIsNull();

        // Crear lista para almacenar los DTOs
        List<ArticuloDTO> listaArticulosActivosDTO = new ArrayList<>();

        // Convertir cada Articulo a ArticuloDTO
        for (Articulo articulo : listaArticulosActivos) {
            ArticuloDTO dto = crearArticuloDTO(articulo);
            listaArticulosActivosDTO.add(dto);
        }

        return listaArticulosActivosDTO;
    }

    //listarArticulosDadosDeBaja
    @Transactional
    public List<ArticuloDTO> listarArticulosDadosDeBaja() {

        List<Articulo> listaArticulosInactivos = articuloRepository.findByFechaHoraBajaArtIsNotNull();

        // Crear lista para almacenar los DTOs
        List<ArticuloDTO> listaArticulosInacctivosDTO = new ArrayList<>();

        // Convertir cada Articulo a ArticuloDTO
        for (Articulo articulo : listaArticulosInactivos) {
            ArticuloDTO dto = crearArticuloDTO(articulo);
            listaArticulosInacctivosDTO.add(dto);
        }
        return listaArticulosInacctivosDTO;
    }

    //listaProveedorPorArticulo
    @Transactional
    public List<ProveedorDTO> listarProveedoresPorArticulo(ArticuloDTO articuloDTO) throws Exception {

        //Buscar artículo
        Articulo articulo = findById(articuloDTO.getId());
        //Buscar lista de proveedores activos por artículo.
        List<Proveedor> listaProveedoresProveedoresActivosPorArticulo = proveedorService.findProveedoresActivosByArticuloId(articuloDTO.getId());
        // Crear lista de ProveedorDTO
        List<ProveedorDTO> listaProveedoresProveedoresActivosPorArticuloDTO = new ArrayList<>();

        for (Proveedor proveedor : listaProveedoresProveedoresActivosPorArticulo) {
            ProveedorDTO proveedorDTO = crearProveedorDTO(proveedor);
            listaProveedoresProveedoresActivosPorArticuloDTO.add(proveedorDTO);
        }

        return listaProveedoresProveedoresActivosPorArticuloDTO;

    }

    //listarArticulosFaltantes
    @Transactional
    public List<ArticuloDTO> listarArticulosFaltantes() {
        List<Articulo> articulosFaltantes = articuloRepository.findArticulosFaltantes();

        List<ArticuloDTO> articulosFaltantesDTO = new ArrayList<>();
        for (Articulo articulo : articulosFaltantes) {
            ArticuloDTO dto = crearArticuloDTO(articulo);
            articulosFaltantesDTO.add(dto);
        }
        return articulosFaltantesDTO;
    }

    //listarProductosAReponer. Productos de lote fijo que han alcanzado R.
    @Transactional
    public List<ArticuloDTO> listarProductosAReponer(){

        List<ProveedorArticulo> listaProveedorArticulo = proveedorArticuloService.findByFechaHoraBajaArtProvIsNull();
        List <Articulo> listaArticulos = new ArrayList<Articulo>();

        for (ProveedorArticulo proveedorArticulo : listaProveedorArticulo){

            if(proveedorArticulo.getTipoLote() == TipoLote.LOTEFIJO && proveedorArticulo.getPuntoPedido() == proveedorArticulo.getArt().getStock()){
                listaArticulos.add(proveedorArticulo.getArt());
            }
        }

        List<ArticuloDTO> listaDTO = new ArrayList<>();
        for (Articulo articulo : listaArticulos) {
            listaDTO.add(crearArticuloDTO(articulo));
        }
        return listaDTO;

    }


    //Métodos auxiliares.

    //crearArticuloDTO. NO TRAE ProveedorArticuloDTO para evitar bucles
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

    //crearProveedorDTO. NO TRAE ArticuloDTO para evitar Bucles.
    private ProveedorDTO crearProveedorDTO(Proveedor proveedor) {
        ProveedorDTO dto = new ProveedorDTO();

        dto.setId(proveedor.getId());
        dto.setCodProv(proveedor.getCodProv());
        dto.setNomProv(proveedor.getNomProv());
        dto.setDescripcionProv(proveedor.getDescripcionProv());
        dto.setFechaHoraBajaProv(proveedor.getFechaHoraBajaProv());

//        if (proveedor.getProveedorArticulos() != null && !proveedor.getProveedorArticulos().isEmpty()) {
//            List<ProveedorArticuloDTO> listaDTO = proveedor.getProveedorArticulos().stream()
//                    .map(this::crearProveedorArticuloDTO)
//                    .collect(Collectors.toList());
//            dto.setProveedorArticulos(listaDTO);
//        }

        return dto;
    }

    //comprobarOrdenCompra Pendiente (No verifica que el artículo exista)
    @Transactional
    private Boolean comprobarOrdenDeCompraPendienteOEnviada(Long idArticulo) {
        // Buscar órdenes de compra con estado PENDIENTE
        List<OrdenCompra> ordenesPendientes = ordenCompraService.buscarOrdenCompraPorEstado("PENDIENTE");

        // Buscar órdenes de compra con estado ENVIADA
        List<OrdenCompra> ordenesEnviadas = ordenCompraService.buscarOrdenCompraPorEstado("ENVIADA");

        // Unir ambas listas
        List<OrdenCompra> ordenes = new ArrayList<>();
        ordenes.addAll(ordenesPendientes);
        ordenes.addAll(ordenesEnviadas);

        // Verificar si alguna orden corresponde al artículo dado
        for (OrdenCompra orden : ordenes) {
            Articulo articulo = orden.getArticulo();
            if (articulo != null && articulo.getId().equals(idArticulo)) {
                return true; // Existe una orden pendiente o enviada para el artículo
            }
        }

        return false; // No se encontró ninguna
    }

    @Transactional
    private Boolean comprobarStockAgotado(Long id) throws Exception {

        //Crear Articulo
        Articulo articulo = findById(id);

        if (articulo.getStock() != 0) {
            return true;
        }
        return false;
    }

    //Busca un ProveedorArticulo no nulo
    @Transactional
    private Optional<ProveedorArticulo> obtenerProveedorArticuloRelacionado(List<ProveedorArticulo> listaProveedorArticulo, Articulo articulo) {

        if (listaProveedorArticulo == null || articulo == null) return Optional.empty();

        for (ProveedorArticulo pa : listaProveedorArticulo) {
            if (pa.getFechaHoraBajaArtProv() == null &&
                    pa.getArt() != null &&
                    pa.getArt().getId().equals(articulo.getId())) {
                return Optional.of(pa);
            }
        }
        return Optional.empty();
    }

    //findByCodArt
    @Transactional
    public Optional<Articulo> findByCodArt(String codArt) {
        return articuloRepository.findByCodArt(codArt);
    }

    @Override
    @Transactional
    public boolean existsById(Long id) {
        return articuloRepository.existsById(id);
    }


}

