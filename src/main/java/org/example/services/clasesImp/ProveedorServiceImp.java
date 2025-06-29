package org.example.services.clasesImp;
import jakarta.transaction.Transactional;
import org.example.dto.ArticuloDTO;
import org.example.dto.ProveedorArticuloDTO;
import org.example.dto.ProveedorDTO;
import org.example.entity.Articulo;
import org.example.entity.OrdenCompra;
import org.example.entity.Proveedor;
import org.example.entity.ProveedorArticulo;
import org.example.repository.BaseRepository;
import org.example.repository.ProveedorRepository;
import org.example.services.BaseServiceImpl;
import org.example.services.EstrategiaCalculoInventario.EstrategiaCalculoInventario;
import org.example.services.EstrategiaCalculoInventario.FabricaEstrategiaCalculoInventario;
import org.example.services.interfaces.ArticuloService;
import org.example.services.interfaces.OrdenCompraService;
import org.example.services.interfaces.ProveedorArticuloService;
import org.example.services.interfaces.ProveedorService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProveedorServiceImp extends BaseServiceImpl<Proveedor, Long> implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ArticuloService articuloService;
    private final FabricaEstrategiaCalculoInventario fabricaEstrategiaCalculoInventario;
    private final OrdenCompraService ordenCompraService;
    private final ProveedorArticuloService proveedorArticuloService;

    public ProveedorServiceImp(BaseRepository<Proveedor, Long> baseRepository, ProveedorRepository proveedorRepository, @Lazy ArticuloService articuloService, FabricaEstrategiaCalculoInventario fabricaEstrategiaCalculoInventario, OrdenCompraService ordenCompraService, ProveedorArticuloService proveedorArticuloService) {
        super(baseRepository);
        this.proveedorRepository = proveedorRepository;
        this.articuloService = articuloService;
        this.fabricaEstrategiaCalculoInventario = fabricaEstrategiaCalculoInventario;
        this.ordenCompraService = ordenCompraService;
        this.proveedorArticuloService = proveedorArticuloService;
    }

    //Métodos

    //altaProveedor
    @Transactional
    public ProveedorDTO altaProveedor(ProveedorDTO proveedorDTO) throws Exception {

        // Validar código único
        if (findByCodProv(proveedorDTO.getCodProv()).isPresent()) {
            throw new Exception("El código ya está en uso");
        }

        // Validar que haya al menos un ProveedorArticulo
        if (proveedorDTO.getProveedorArticulos() == null || proveedorDTO.getProveedorArticulos().isEmpty()) {
            throw new Exception("Debe seleccionar al menos un artículo.");
        }

        // Validar que para cada ProveedorArticulo, su Articulo exista.
        for (ProveedorArticuloDTO paDTO : proveedorDTO.getProveedorArticulos()) {
            if (paDTO.getArticuloDTO() == null ) {
                throw new Exception("Cada ProveedorArticulo debe tener un Artículo válido.");
            }
            if (!articuloService.existsById(paDTO.getArticuloDTO().getId())) {
                throw new Exception("El artículo con id " + paDTO.getArticuloDTO().getId() + " no existe.");
            }
        }

        // Mapear ProveedorDTO a entidad Proveedor
        Proveedor nuevoProveedor = new Proveedor();
        nuevoProveedor.setCodProv(proveedorDTO.getCodProv());
        nuevoProveedor.setNomProv(proveedorDTO.getNomProv());
        nuevoProveedor.setDescripcionProv(proveedorDTO.getDescripcionProv());

        // Crear y asociar ProveedorArticulo
        List<ProveedorArticulo> listaProveedorArticulo = new ArrayList<>();
        for (ProveedorArticuloDTO paDTO : proveedorDTO.getProveedorArticulos()) {

            ProveedorArticulo pa = new ProveedorArticulo();

            // Traer el artículo desde la BD
            Articulo articulo = articuloService.findById(paDTO.getArticuloDTO().getId());
            pa.setArt(articulo);

            // Setear atributos cargados
            pa.setDemoraEntrega(paDTO.getDemoraEntrega());
            pa.setNivelDeServicio(paDTO.getNivelDeServicio());
            pa.setCostoUnitario(paDTO.getCostoUnitario());
            pa.setCostoPedido(paDTO.getCostoPedido());
            pa.setCostoMantenimiento(paDTO.getCostoMantenimiento());
            pa.setPeriodoRevision(paDTO.getPeriodoRevision());
            pa.setTipoLote(paDTO.getTipoLote());

            //Aplicar estrategia de cálculo según tipo de lote
            EstrategiaCalculoInventario estrategia = fabricaEstrategiaCalculoInventario.obtener(pa.getTipoLote());
            pa = estrategia.calcular(pa); // Se actualiza el objeto con cálculos

            listaProveedorArticulo.add(pa);
        }

        nuevoProveedor.setProveedorArticulos(listaProveedorArticulo);

        // Guardar proveedor (y ProveedorArticulo en cascada)
        Proveedor proveedorGuardado = save(nuevoProveedor);

        return crearProveedorDTO(proveedorGuardado);
    }

    //modificarProveedor
    @Transactional
    public ProveedorDTO modificarProveedor(ProveedorDTO proveedorDTO) throws Exception {

        // Buscar proveedor
        Proveedor proveedorExistente = findById(proveedorDTO.getId());

        if (proveedorExistente == null) {
            throw new Exception("El proveedor no existe");
        }

        if (proveedorExistente.getFechaHoraBajaProv() != null) {
            throw new Exception("El proveedor ya fue dado de baja");
        }

        // Comprobar Órdenes de Compra pendientes o Enviadas.
        for (ProveedorArticulo pa : proveedorExistente.getProveedorArticulos()) {
            if (comprobarOrdenDeCompraPendienteOEnviada(pa.getId())) {
                throw new Exception("No se puede modificar, existen órdenes de compra pendientes o enviadas.");
            }
        }

        proveedorExistente.setNomProv(proveedorDTO.getNomProv());
        proveedorExistente.setDescripcionProv(proveedorDTO.getDescripcionProv());

        List<ProveedorArticulo> listaProveedorArticuloModificado = new ArrayList<>();

        for (ProveedorArticuloDTO paDTO : proveedorDTO.getProveedorArticulos()) {

            if (paDTO.getId() != null && paDTO.getId() != 0) {
                Optional<ProveedorArticulo> proveedorArticuloOptional = Optional.ofNullable(proveedorArticuloService.findById(paDTO.getId()));

                if (proveedorArticuloOptional.isPresent()) {
                    ProveedorArticulo proveedorArticuloExistente = proveedorArticuloOptional.get();

                    // Baja lógica si trae fecha de baja
                    if (paDTO.getFechaHoraBajaArtProv() != null) {
                        proveedorArticuloExistente.setFechaHoraBajaArtProv(paDTO.getFechaHoraBajaArtProv());
                    } else {
                        proveedorArticuloExistente.setDemoraEntrega(paDTO.getDemoraEntrega());
                        proveedorArticuloExistente.setNivelDeServicio(paDTO.getNivelDeServicio());
                        proveedorArticuloExistente.setCostoUnitario(paDTO.getCostoUnitario());
                        proveedorArticuloExistente.setCostoPedido(paDTO.getCostoPedido());
                        proveedorArticuloExistente.setCostoMantenimiento(paDTO.getCostoMantenimiento());
                        proveedorArticuloExistente.setPeriodoRevision(paDTO.getPeriodoRevision());
                        proveedorArticuloExistente.setFechaHoraBajaArtProv(null);
                        proveedorArticuloExistente.setTipoLote(paDTO.getTipoLote());
                    }

                    listaProveedorArticuloModificado.add(proveedorArticuloExistente);
                    continue;
                }
            }

            // Nuevo ProveedorArticulo
            if (paDTO.getArticuloDTO() == null || paDTO.getArticuloDTO().getId() == null) {
                throw new Exception("Falta el ID del artículo para crear ProveedorArticulo");
            }

            Optional<Articulo> articuloExistenteOptional = Optional.ofNullable(articuloService.findById(paDTO.getArticuloDTO().getId()));

            if (articuloExistenteOptional.isPresent()) {
                ProveedorArticulo nuevoPA = new ProveedorArticulo();
                nuevoPA.setArt(articuloExistenteOptional.get());
                nuevoPA.setDemoraEntrega(paDTO.getDemoraEntrega());
                nuevoPA.setNivelDeServicio(paDTO.getNivelDeServicio());
                nuevoPA.setCostoUnitario(paDTO.getCostoUnitario());
                nuevoPA.setCostoPedido(paDTO.getCostoPedido());
                nuevoPA.setCostoMantenimiento(paDTO.getCostoMantenimiento());
                nuevoPA.setPeriodoRevision(paDTO.getPeriodoRevision());
                nuevoPA.setFechaHoraBajaArtProv(null);
                nuevoPA.setTipoLote(paDTO.getTipoLote());
                listaProveedorArticuloModificado.add(nuevoPA);
            } else {
                throw new Exception("No existe el artículo con ID " + paDTO.getArticuloDTO().getId());
            }
        }

        if (listaProveedorArticuloModificado.isEmpty()) {
            throw new Exception("No hay proveedores artículo para modificar");
        }

        // Recalcular solo los activos
        List<ProveedorArticulo> proveedorArticuloRecalculado = new ArrayList<>();
        for (ProveedorArticulo pa : listaProveedorArticuloModificado) {
            if (pa.getFechaHoraBajaArtProv() == null) {
                EstrategiaCalculoInventario estrategiaCalculoInventario = fabricaEstrategiaCalculoInventario.obtener(pa.getTipoLote());
                proveedorArticuloRecalculado.add(estrategiaCalculoInventario.calcular(pa));
            } else {
                // Mantener los dados de baja sin recalcular
                proveedorArticuloRecalculado.add(pa);
            }
        }

        proveedorExistente.setProveedorArticulos(proveedorArticuloRecalculado);

        Proveedor proveedorModficado = update(proveedorExistente.getId(), proveedorExistente);

        return crearProveedorDTO(proveedorModficado);
    }

    //modificarProveedor
//    @Transactional
//    public ProveedorDTO modificarProveedor (ProveedorDTO proveedorDTO) throws  Exception{
//
//        //Buscar proveedor
//        Proveedor proveedorExistente = findById(proveedorDTO.getId());
//
//        //Ver si existe
//        if (proveedorExistente == null){
//            throw new  Exception("El proveedor no existe");
//        }
//
//        //Fue dado de baja antes
//        if (proveedorExistente.getFechaHoraBajaProv() != null){
//            throw new  Exception("El proveedor ya fue dado de baja");
//        }
//
//        //Comprobar Órdenes de Compra pendientes o Enviadas.
//        for(ProveedorArticulo pa :proveedorExistente.getProveedorArticulos()){
//            if (comprobarOrdenDeCompraPendienteOEnviada(pa.getId())){
//                throw new Exception("No se puede moficar, existen órdenes de compra pendietes o enviadas.");
//            }
//        }
//
//        //Modificar atributos básicos
//        proveedorExistente.setNomProv(proveedorDTO.getNomProv());
//        proveedorExistente.setDescripcionProv(proveedorDTO.getDescripcionProv());
//
//        //Revisar atributos avanzados.
//
//        //Comprobar que estos proveedorArticulo existen o estan vinculados a un artículo existente.
//
//        List<ProveedorArticulo> listaProveedorArticuloModificado = new ArrayList<ProveedorArticulo>();
//
//        //En caso de que no existan, quiere decir que se quiere cargar un articulo, no necesariamente es un error. Sino es que se quiere añadir un nuevo articulo al proveedor
//        for (ProveedorArticuloDTO paDTO : proveedorDTO.getProveedorArticulos()){
//
//            Optional<ProveedorArticulo> proveedorArticuloOptional = Optional.ofNullable(proveedorArticuloService.findById(paDTO.getId()));
//
//            // Comprobar si existe en la base de datos el proveedor Articulo, esto es modificar un ProveedorArticulo existente.
//            if (proveedorArticuloOptional.isPresent() ){
//
//                ProveedorArticulo proveedorArticuloExistente = proveedorArticuloService.findById(paDTO.getId());
//
//                proveedorArticuloExistente.setDemoraEntrega(paDTO.getDemoraEntrega());
//                proveedorArticuloExistente.setNivelDeServicio(paDTO.getNivelDeServicio());
//                proveedorArticuloExistente.setCostoUnitario(paDTO.getCostoUnitario());
//                proveedorArticuloExistente.setCostoPedido(paDTO.getCostoPedido());
//                proveedorArticuloExistente.setCostoMantenimiento(paDTO.getCostoMantenimiento());
//                proveedorArticuloExistente.setPeriodoRevision(paDTO.getPeriodoRevision());
//                proveedorArticuloExistente.setFechaHoraBajaArtProv(null);
//
//                //Añadir a la lista de Modificados
//                listaProveedorArticuloModificado.add(proveedorArticuloExistente); } //Aquí se añaden los proveedoArticuloExistentes.
//
//            //Si no existe, se quiere añadir un nuevo artículo al proveedor. Se debe crear una nueva instancia de ProveedorArticulo, esto es añadir a un nuevo artílo al proveedor.
//            else {
//
//                Optional<Articulo> articuloExistenteOptional = Optional.of(articuloService.findById(paDTO.getArticuloDTO().getId()));
//
//                if (articuloExistenteOptional.isPresent()) {
//
//                    ProveedorArticulo nuevoPA = new ProveedorArticulo();
//
//                    // Asociar artículo existente
//                    nuevoPA.setArt(articuloExistenteOptional.get());
//
//                    // Setear los atributos cargables (los que vienen del DTO)
//                    nuevoPA.setDemoraEntrega(paDTO.getDemoraEntrega());
//                    nuevoPA.setNivelDeServicio(paDTO.getNivelDeServicio());
//                    nuevoPA.setCostoUnitario(paDTO.getCostoUnitario());
//                    nuevoPA.setCostoPedido(paDTO.getCostoPedido());
//                    nuevoPA.setCostoMantenimiento(paDTO.getCostoMantenimiento());
//                    nuevoPA.setPeriodoRevision(paDTO.getPeriodoRevision());
//                    nuevoPA.setFechaHoraBajaArtProv(null);
//
//                    //Añadir a la lista de modificados.
//                    listaProveedorArticuloModificado.add(nuevoPA);
//
//                }
//            }
//        }
//
//        //Revisar si la lista está vacía. Supongo que no debería pasar porque se tiene que crear un proveedor si o si con un articulo.
//        if (listaProveedorArticuloModificado.isEmpty()){
//            throw new Exception("No hay proveedores artículo para modificar");
//        }
//
//        //Añadir nueva lista de ProveedorArticulo.
//        proveedorExistente.setProveedorArticulos(listaProveedorArticuloModificado);
//
//        //Actualizar al proveedor.
//        Proveedor proveedorModficado = update(proveedorExistente.getId(),proveedorExistente);
//
//        return crearProveedorDTO(proveedorModficado);
//
//    }

    //bajaProveedor.
    @Transactional
    public ProveedorDTO bajaProveedor(Long id) throws  Exception {

        //Buscar proveedor
        Proveedor proveedorExistente = findById(id);

        //Ver si existe
        if (proveedorExistente == null){
            throw new  Exception("El proveedor no existe");
        }

        //Fue dado de baja antes
        if (proveedorExistente.getFechaHoraBajaProv() != null){
            throw new Exception ("El proveedor ya fue dado de baja");
        }

        //Comprobar Órdenes de Compra pendientes o Enviadas.
        for(ProveedorArticulo pa :proveedorExistente.getProveedorArticulos()){

            Long proveedorElegidoID = pa.getArt().getProveedorElegidoID();
            if ((proveedorElegidoID != null && proveedorElegidoID.equals(proveedorExistente.getId()))
                    || comprobarOrdenDeCompraPendienteOEnviada(pa.getArt().getId())) {
                throw new Exception("No se puede dar de baja");
            }

            pa.setFechaHoraBajaArtProv(new Date());

        }

        proveedorExistente.setFechaHoraBajaProv(new Date());
        update(proveedorExistente.getId(),proveedorExistente);

        return crearProveedorDTO(proveedorExistente);
    }

    //listarArticulosPorProveedor
    @Transactional
    public List<ArticuloDTO> listarArticulosPorProveedor(ProveedorDTO proveedorDTO) throws Exception {
        Proveedor proveedor = findById(proveedorDTO.getId());

        // Verificar si existe
        if (proveedor == null) {
            throw new Exception("El proveedor no existe");
        }

        // Verificar si fue dado de baja
        if (proveedor.getFechaHoraBajaProv() != null) {
            throw new Exception("Ya fue dado de baja");
        }

        List<ArticuloDTO> listaArticuloPorProveedorDTO = new ArrayList<>();

        for (ProveedorArticulo pa : proveedor.getProveedorArticulos()) {
            // Solo agregar artículos activos
            if (pa.getArt().getFechaHoraBajaArt() == null) {
                listaArticuloPorProveedorDTO.add(crearArticuloDTO(pa.getArt()));
            }
        }
        return listaArticuloPorProveedorDTO;
    }

    @Override
    @Transactional
    public Optional<Proveedor> findByCodProv(String codProv) {
        return proveedorRepository.findByCodProv(codProv);
    }

    //Lo uso en maestro de artículos.
    @Override
    @Transactional
    public List<Proveedor> findProveedoresActivosByArticuloId(Long articuloId) {
        return proveedorRepository.findProveedoresActivosByArticuloId(articuloId);
    }

    @Transactional
    public List<ProveedorDTO> obtenerTodosLosProveedores(){

        List<Proveedor> proveedores = proveedorRepository.findAll();
        List<ProveedorDTO> proveedorDTOs= new ArrayList<>();
        for (Proveedor proveedor : proveedores){

            proveedorDTOs.add(crearProveedorDTO(proveedor));

        }
        return proveedorDTOs;
    }


    //Métodos Auxiliares

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
        dto.setDesviacionEstandar(articulo.getDesviacionEstandar());


        if (articulo.getProveedorElegidoID() != null) {
            ProveedorDTO proveedorDTO = new ProveedorDTO();
            proveedorDTO.setId(articulo.getProveedorElegidoID());
            dto.setProveedorDTO(proveedorDTO);
        }
        return dto;
    }

    //crearProveedorDTO
    public ProveedorDTO crearProveedorDTO(Proveedor proveedor) {
        ProveedorDTO dto = new ProveedorDTO();

        dto.setId(proveedor.getId());
        dto.setCodProv(proveedor.getCodProv());
        dto.setNomProv(proveedor.getNomProv());
        dto.setDescripcionProv(proveedor.getDescripcionProv());
        dto.setFechaHoraBajaProv(proveedor.getFechaHoraBajaProv());

        if (proveedor.getProveedorArticulos() != null && !proveedor.getProveedorArticulos().isEmpty()) {
            List<ProveedorArticuloDTO> listaDTO = proveedor.getProveedorArticulos().stream()
                    .map(this::crearProveedorArticuloDTO)
                    .collect(Collectors.toList());
            dto.setProveedorArticulos(listaDTO);
        }

        return dto;
    }

    //crearProveedorArticuloDTO
    private ProveedorArticuloDTO crearProveedorArticuloDTO(ProveedorArticulo proveedorArticulo) {
        ProveedorArticuloDTO dto = new ProveedorArticuloDTO();

        dto.setId(proveedorArticulo.getId());
        dto.setFechaHoraBajaArtProv(proveedorArticulo.getFechaHoraBajaArtProv());
        dto.setCostoGeneralInventario(proveedorArticulo.getCostoGeneralInventario());
        dto.setDemoraEntrega(proveedorArticulo.getDemoraEntrega());
        dto.setNivelDeServicio(proveedorArticulo.getNivelDeServicio());
        dto.setCostoUnitario(proveedorArticulo.getCostoUnitario());
        dto.setCostoPedido(proveedorArticulo.getCostoPedido());
        dto.setCostoMantenimiento(proveedorArticulo.getCostoMantenimiento());
        dto.setLoteOptimo(proveedorArticulo.getLoteOptimo());
        dto.setPuntoPedido(proveedorArticulo.getPuntoPedido());
        dto.setCantidadAPedir(proveedorArticulo.getCantidadAPedir());
        dto.setInventarioMaximo(proveedorArticulo.getInventarioMaximo());
        dto.setPeriodoRevision(proveedorArticulo.getPeriodoRevision());
        dto.setTipoLote(proveedorArticulo.getTipoLote());

        // Mapear Articulo (solo id y nombre si no querés cargar toda la entidad)
        if (proveedorArticulo.getArt() != null) {
            ArticuloDTO articuloDTO = new ArticuloDTO();
            articuloDTO.setId(proveedorArticulo.getArt().getId());
            articuloDTO.setNomArt(proveedorArticulo.getArt().getNomArt());
            dto.setArticuloDTO(articuloDTO);
        }

        return dto;
    }

    //comprobarOrdenCompra Pendiente (No verifica que el artículo exista)
    @Transactional
    private Boolean comprobarOrdenDeCompraPendienteOEnviada(Long idArticulo) {
        // Buscar órdenes de compra con estado PENDIENTE
        List<OrdenCompra> ordenesPendientes = ordenCompraService.buscarOrdenCompraPorEstado("PENDIENTE");

        // Buscar órdenes de compra con estado ENVIADA
        List<OrdenCompra> ordenesEnviadas = ordenCompraService.buscarOrdenCompraPorEstado("EN CURSO");

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

    //crearProveedorDTO
    public ProveedorDTO crearProveedorDTOSinPA(Proveedor proveedor) {
        ProveedorDTO dto = new ProveedorDTO();

        dto.setId(proveedor.getId());
        dto.setCodProv(proveedor.getCodProv());
        dto.setNomProv(proveedor.getNomProv());
        dto.setDescripcionProv(proveedor.getDescripcionProv());
        dto.setFechaHoraBajaProv(proveedor.getFechaHoraBajaProv());

        return dto;
    }


}
