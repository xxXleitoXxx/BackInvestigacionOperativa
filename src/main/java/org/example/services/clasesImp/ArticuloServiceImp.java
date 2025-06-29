package org.example.services.clasesImp;
import jakarta.transaction.Transactional;
import org.example.dto.ArticuloDTO;
import org.example.dto.ArticuloProvDTO;
import org.example.dto.ProveedorDTO;
import org.example.entity.Articulo;
import org.example.entity.OrdenCompra;
import org.example.entity.Proveedor;
import org.example.entity.ProveedorArticulo;
import org.example.enums.TipoLote;
import org.example.repository.ArticuloRepository;
import org.example.repository.BaseRepository;
import org.example.repository.ProveedorRepository;
import org.example.services.BaseServiceImpl;
import org.example.services.EstrategiaCalculoInventario.EstrategiaCalculoInventario;
import org.example.services.EstrategiaCalculoInventario.FabricaEstrategiaCalculoInventario;
import org.example.services.interfaces.ArticuloService;
import org.example.services.interfaces.ProveedorArticuloService;
import org.example.services.interfaces.OrdenCompraService;
import org.example.services.interfaces.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class ArticuloServiceImp extends BaseServiceImpl<Articulo,Long> implements ArticuloService {

    // Repositorio.
    private final ArticuloRepository articuloRepository;
    @Autowired
    private ProveedorRepository proveedorRepository;


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

        // Validar código duplicado
        if (findByCodArt(articuloDTO.getCodArt()).isPresent()) {
            throw new Exception("Ya existe un artículo con el código: " + articuloDTO.getCodArt());
        }

        // Crear Articulo en memoria
        Articulo articuloNuevo = new Articulo();
        articuloNuevo.setCodArt(articuloDTO.getCodArt());
        articuloNuevo.setNomArt(articuloDTO.getNomArt());
        articuloNuevo.setPrecioVenta(articuloDTO.getPrecioVenta());
        articuloNuevo.setDescripcionArt(articuloDTO.getDescripcionArt());
        articuloNuevo.setStock(articuloDTO.getStock());
        articuloNuevo.setDemandaDiaria(articuloDTO.getDemandaDiaria());
        articuloNuevo.setDesviacionEstandar(articuloDTO.getDesviacionEstandar());


        // Guardar artículo
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
        articulo.setProveedorElegidoID(null); // Limpiar proveedor elegido al dar de baja

        //Dar de baja ProveedorArticulo asociado al artículo
        proveedorArticuloService.findByArticuloIdAndFechaHoraBajaArtProvIsNull(articulo.getId())
                .forEach(pa -> {
                    pa.setFechaHoraBajaArtProv(new Date());
                    try {
                        proveedorArticuloService.update(pa.getId(), pa);
                    } catch (Exception e) {
                        throw new RuntimeException ("Error al actualizar ProveedorArticulo: " + pa.getId(), e);
                    }
                });

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

        // Comprobar que no fue dado de baja anteriormente
        if (articuloExistente.getFechaHoraBajaArt() != null) {
            throw new Exception("El artículo ya fue dado de baja");
        }

        // Comprobar que no tenga órdenes de compra pendientes o enviadas
        if (comprobarOrdenDeCompraPendienteOEnviada(articuloDTO.getId())) {
            throw new Exception("El artículo tiene órdenes de compra pendientes o enviadas y no puede ser modificado");
        }

        // Actualizar campos básicos
        if (articuloDTO.getNomArt() != null && !articuloDTO.getNomArt().isBlank()) {
            articuloExistente.setNomArt(articuloDTO.getNomArt());
        }
        if (articuloDTO.getPrecioVenta() != null && articuloDTO.getPrecioVenta() > 0) {
            articuloExistente.setPrecioVenta(articuloDTO.getPrecioVenta());
        }

        // --- Recalcular ProveedorArticulo si cambian stock, demandaDiaria o desviacionEstandar ---
        boolean recalcular = false;

        if (articuloDTO.getStock() != articuloExistente.getStock()) {
            articuloExistente.setStock(articuloDTO.getStock());
            recalcular = true;
        }
        if (articuloDTO.getDemandaDiaria() != articuloExistente.getDemandaDiaria()) {
            articuloExistente.setDemandaDiaria(articuloDTO.getDemandaDiaria());
            recalcular = true;
        }
        if (articuloDTO.getDesviacionEstandar() != articuloExistente.getDesviacionEstandar()) {
            articuloExistente.setDesviacionEstandar(articuloDTO.getDesviacionEstandar());
            recalcular = true;
        }

        if (recalcular) {
            List<ProveedorArticulo> proveedorArticulos = proveedorArticuloService.findActivosByArticuloId(articuloExistente.getId());
            for (ProveedorArticulo pa : proveedorArticulos) {
                EstrategiaCalculoInventario estrategia = fabricaEstrategiaCalculoInventario.obtener(pa.getTipoLote());
                estrategia.calcular(pa);
            }
        }
        // ----------------------------------------------------------------------

        // Verificar si se quiere cambiar el proveedor predeterminado.
        if (articuloDTO.getProveedorDTO() != null  && articuloDTO.getProveedorDTO().getId() != null ) {

            Proveedor nuevoProveedorElegido = proveedorService.findById(articuloDTO.getProveedorDTO().getId());
            System.out.println(articuloDTO.getProveedorDTO().getId());
            System.out.println(articuloDTO.getProveedorDTO().getNomProv());
            if (nuevoProveedorElegido == null) {
                throw new Exception("El proveedor especificado no existe.");
            }

            if (nuevoProveedorElegido.getFechaHoraBajaProv() != null){
                throw new Exception("El proveedor ya fue dado de baja");
            }

            //Buscar instancia de ProveedorArticulo asociada al articulo y al proveedor. Tiene que estar activa.
            Optional<ProveedorArticulo> proveedorArticuloOptional = proveedorArticuloService.buscarInstanciaActivaProveedorArticuloSegunProveedorYArticulo(nuevoProveedorElegido.getId(),articuloExistente.getId());

            if (proveedorArticuloOptional.isEmpty()){
                throw new Exception("El proveedor elegido no trabaja con este artículo");
            }

            //Recalcular valores al ingresar el nuevo Proveedor.
            EstrategiaCalculoInventario estrategiaCalculoInventario = fabricaEstrategiaCalculoInventario.obtener(proveedorArticuloOptional.get().tipoLote);
            estrategiaCalculoInventario.calcular(proveedorArticuloOptional.get());

            // Si pasa la validación, se asigna el proveedor
            articuloExistente.setProveedorElegidoID(nuevoProveedorElegido.getId());

        }

        //Guardar el nuevo articulo
        Articulo articuloModificado = update(articuloExistente.getId(), articuloExistente);

        return crearArticuloDTO(articuloModificado);

    }

    //modificarParámetrosInventario
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
        articulo.setDesviacionEstandar(articuloDTO.getDesviacionEstandar());
        articulo.setStock(articuloDTO.getStock());

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
    public List<ArticuloProvDTO> listarProveedoresPorArticulo(ArticuloDTO articuloDTO) throws Exception {

        //Buscar artículo
        Articulo articulo = findById(articuloDTO.getId());
        //Buscar lista de proveedores activos por artículo.
        List<Proveedor> listaProveedoresProveedoresActivosPorArticulo = proveedorService.findProveedoresActivosByArticuloId(articuloDTO.getId());
        // Crear lista de ProveedorDTO
        List<ArticuloProvDTO> listaProveedoresProveedoresActivosPorArticuloDTO = new ArrayList<>();

        for (Proveedor proveedor : listaProveedoresProveedoresActivosPorArticulo) {
            ArticuloProvDTO proveedorDTO = crearProveedorDTO(proveedor);
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

            if(proveedorArticulo.getTipoLote() == TipoLote.LOTEFIJO && proveedorArticulo.getArt().getStockSeguridad() < proveedorArticulo.getArt().getStock() && proveedorArticulo.getArt().getStock() <= proveedorArticulo.getPuntoPedido() ){
                listaArticulos.add(proveedorArticulo.getArt());
            }
        }

        for (Articulo articulo : listaArticulos) {
            if (comprobarOrdenDeCompraPendienteOEnviada(articulo.getId())) {
                listaArticulos.remove(articulo);
            }
        }

        List<ArticuloDTO> listaDTO = new ArrayList<>();
        for (Articulo articulo : listaArticulos) {
            listaDTO.add(crearArticuloDTO(articulo));
        }
        return listaDTO;

    }

    //listarTodos
    @Transactional
    public List<ArticuloDTO> obtenerTodos() throws Exception {
        List<Articulo> todosLosArticulos = findAll();
        List<ArticuloDTO> todosLosArticulosDTO = new ArrayList<>();
        for (Articulo articulo : todosLosArticulos){
            todosLosArticulosDTO.add(crearArticuloDTO(articulo));
        }
        return todosLosArticulosDTO;
    }


    //Métodos auxiliares. -----------------------------------------------------------------------------------------------------------------------------

    //crearArticuloDTO. NO TRAE ProveedorArticuloDTO para evitar bucles
    private ArticuloDTO crearArticuloDTO(Articulo articulo) {
        ArticuloDTO dto = new ArticuloDTO();

        //tiene que traer el articulo y traer por lo menos el nombre y el id de proveedor

        if(articulo.getProveedorElegidoID() != null){

            Optional<Proveedor> prov = proveedorRepository.findById(articulo.getProveedorElegidoID());
            ProveedorDTO provdto = new ProveedorDTO();
            provdto.setId(prov.get().getId());
            provdto.setNomProv(prov.get().getNomProv());
            dto.setProveedorDTO(provdto);
        }

        //Solo seteo nombre y id
        dto.setId(articulo.getId());
        dto.setCodArt(articulo.getCodArt());
        dto.setNomArt(articulo.getNomArt());
        dto.setPrecioVenta(articulo.getPrecioVenta());
        dto.setDescripcionArt(articulo.getDescripcionArt());
        dto.setFechaHoraBajaArt(articulo.getFechaHoraBajaArt());
        dto.setStock(articulo.getStock());
        dto.setStockSeguridad(articulo.getStockSeguridad());
        dto.setDemandaDiaria(articulo.getDemandaDiaria());
        dto.setDesviacionEstandar(articulo.getDesviacionEstandar());


//        if (articulo.getProveedorElegido() != null) {
//            ProveedorDTO proveedorDTO = new ProveedorDTO();
//            proveedorDTO.setId(articulo.getProveedorElegido().getId());
//
//            dto.setProveedorDTO(proveedorDTO);
//        }

        return dto;
    }

    //crearProveedorDTO. NO TRAE ArticuloDTO para evitar Bucles.
    private ArticuloProvDTO crearProveedorDTO(Proveedor proveedor) {
        ArticuloProvDTO dto = new ArticuloProvDTO();

        dto.setId(proveedor.getId());
        dto.setNomProv(proveedor.getNomProv());

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
        List<OrdenCompra> ordenesPendientes = ordenCompraService.buscarOrdenCompraPorEstado("EOC001");

        // Buscar órdenes de compra con estado ENVIADA
        List<OrdenCompra> ordenesEnviadas = ordenCompraService.buscarOrdenCompraPorEstado("EOC002");

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

