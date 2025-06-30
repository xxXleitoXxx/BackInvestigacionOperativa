package org.example.services.EstrategiaCalculoInventario;
import org.example.entity.ProveedorArticulo;
import org.example.enums.TipoLote;
import org.springframework.stereotype.Service;

@Service
public class EstrategiaCalculoInventarioLoteFijo implements EstrategiaCalculoInventario {
    @Override
    public ProveedorArticulo calcular(ProveedorArticulo proveedorArticulo) {

        //Variables para cálculo del lote óptimo o punto para volver a pedir R.

        int d = proveedorArticulo.getArt().getDemandaDiaria(); //Demanda anual
        int D = d * 365; //Demanda diaria
        int L = proveedorArticulo.getDemoraEntrega(); //Demora de entrega
        double z; //Números de desvios estandar respecto a la media.
        if(proveedorArticulo.getNivelDeServicio() == 85){ z = 1.036;} else { z=1.645;}
        int o = proveedorArticulo.getArt().getDesviacionEstandar(); //Desvio Estandar.

        //Calcular Stock de seguridad y punto pedido R.

        int stockSeguridad = (int) Math.floor(z * o);
        int R = d*L + stockSeguridad;

        //Calcular cantidadOptima y costo general inventario.

        Float C = proveedorArticulo.getCostoPedido(); //Costo por Unidad
        Float H = proveedorArticulo.getCostoMantenimiento(); //Costo de mantenimiento
        Float S = proveedorArticulo.getCostoPedido();   //Costo de pedido
        int Q = (int) Math.sqrt((2.0 * D * S) / H);
        Float CT = D*C + (D/Q)*S +(Q/2)*H;

        //Setear nuevos valores al ProveedorArticulo.

        proveedorArticulo.setPuntoPedido(R);
        proveedorArticulo.setLoteOptimo(Q);
        proveedorArticulo.setCostoGeneralInventario(CT);

        //Setear nuevos valores al Articulo.

        proveedorArticulo.getArt().setStockSeguridad(stockSeguridad);

        return proveedorArticulo;
    }

    @Override
    public TipoLote getTipoLote() {
        return TipoLote.LOTEFIJO;
    }
}
