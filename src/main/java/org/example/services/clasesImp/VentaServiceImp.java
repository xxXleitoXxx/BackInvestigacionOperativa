package org.example.services.clasesImp;

import org.example.entity.Venta;
import org.example.entity.VentaArticulo;
import org.example.repository.BaseRepository;
import org.example.repository.VentaRepository;
import org.example.services.BaseService;
import org.example.services.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentaServiceImp extends BaseServiceImpl <Venta,Long> implements BaseService <Venta,Long> {

    //Repositorio
    @Autowired
    VentaRepository ventaRepository;

    //Constructor
    public VentaServiceImp(BaseRepository<Venta, Long> baseRespository, VentaRepository ventaRepository) {
        super(baseRespository);
        this.ventaRepository = ventaRepository;
    }

    public boolean controlVenta( Venta venta){
        System.out.println("charu");
        Boolean posibilidad = true;
        for(VentaArticulo va : venta.getVentaArticulos()) {
            int pedido = va.getCantArtVent();
            int disponible = va.getArt().getStock();
            if (pedido > disponible) {
                posibilidad = false;
            }
        }
            return  posibilidad;
    }

    public void ActualizarStock(Venta venta){
        System.out.println("haadddda");
        for(VentaArticulo va : venta.getVentaArticulos()) {
            int Stockrestar= va.getArt().getStock() - va.getCantArtVent();
            va.getArt().setStock(Stockrestar);
        }
    }

}
