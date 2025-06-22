package org.example.services.clasesImp;

import jakarta.transaction.Transactional;
import org.example.dto.ArticuloDTO;
import org.example.dto.ProveedorArticuloDTO;
import org.example.dto.ProveedorDTO;
import org.example.entity.Articulo;
import org.example.entity.Proveedor;
import org.example.entity.ProveedorArticulo;
import org.example.repository.BaseRepository;
import org.example.repository.ProveedorRepository;
import org.example.services.BaseServiceImpl;
import org.example.services.EstrategiaCalculoInventario.EstrategiaCalculoInventario;
import org.example.services.EstrategiaCalculoInventario.FabricaEstrategiaCalculoInventario;
import org.example.services.interfaces.ArticuloService;
import org.example.services.interfaces.ProveedorService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProveedorServiceImp extends BaseServiceImpl<Proveedor, Long> implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ArticuloService articuloService;
    private final FabricaEstrategiaCalculoInventario fabricaEstrategiaCalculoInventario;

    public ProveedorServiceImp(BaseRepository<Proveedor, Long> baseRepository, ProveedorRepository proveedorRepository, @Lazy ArticuloService articuloService, FabricaEstrategiaCalculoInventario fabricaEstrategiaCalculoInventario) {
        super(baseRepository);
        this.proveedorRepository = proveedorRepository;
        this.articuloService = articuloService;
        this.fabricaEstrategiaCalculoInventario = fabricaEstrategiaCalculoInventario;
    }

    //Métodos

    @Transactional
    //altaProveedor
    public ProveedorDTO altaProveedor(ProveedorDTO proveedorDTO) throws Exception {

        // Validar código único
        if (findByCodProv(proveedorDTO.getCodProv()).isPresent()) {
            throw new Exception("El código ya está en uso");
        }

        // Validar que haya al menos un ProveedorArticulo
        if (proveedorDTO.getProveedorArticulos() == null || proveedorDTO.getProveedorArticulos().isEmpty()) {
            throw new Exception("Debe seleccionar al menos un artículo.");
        }

        // Validar cada ProveedorArticulo y que su Articulo exista
        for (ProveedorArticuloDTO paDTO : proveedorDTO.getProveedorArticulos()) {
            if (paDTO.getArticuloDTO() == null || paDTO.getArticuloDTO().getId() == null) {
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

    //bajaProveedor.
    




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

    public List<ProveedorDTO> getProveedores(){
        List<Proveedor> proveedors = proveedorRepository.findAll();
        List<ProveedorDTO> proveedorDtos= new ArrayList<>();
        for(Proveedor p:proveedors){
         ProveedorDTO pd = new ProveedorDTO();

            pd.setCodProv(p.getCodProv());
            pd.setNomProv(p.getNomProv());
            proveedorDtos.add(pd);
        }
        return proveedorDtos;
    }


    //Métodos Auxiliares
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



}
