package org.example.services.EstrategiaCalculoInventario;
import org.example.entity.ProveedorArticulo;
import org.example.enums.TipoLote;
import org.springframework.stereotype.Service;

@Service
public class EstrategiaCalculoInventarioPeriodoFijo implements EstrategiaCalculoInventario {

    @Override
    public ProveedorArticulo calcular(ProveedorArticulo proveedorArticulo) {

        //Variables para cálculo del lote óptimo o punto para volver a pedir R.

        int d = proveedorArticulo.getArt().getDemandaDiaria(); //Demanda diaria
        int D = d * 365; //Demanda anual
        int T = proveedorArticulo.getPeriodoRevision(); //Periodo revision
        int L = proveedorArticulo.getDemoraEntrega(); //Demora de entrega
        int z; //Números de desvios estandar respecto a la media.
        if(proveedorArticulo.getNivelDeServicio() == 85){ z = 1;} else { z=2;}
        int o = proveedorArticulo.getArt().getDesviacionEstandar(); //Desvio Estandar.
        int I = proveedorArticulo.getArt().getStock(); //Stock Actual.

        //Calcular Stock de seguridad y cantidad a pedir q

        int stockSeguridad = z*o*(T+L);
        int q = d*(T+L) + stockSeguridad - I;

        //Calcular cantidadOptima e inventario maximo.

        Float C = proveedorArticulo.getCostoUnitario(); //Costo por Unidad
        Float H = proveedorArticulo.getCostoMantenimiento(); //Costo de mantenimiento
        Float S = proveedorArticulo.getCostoPedido();   //Costo de pedido
        int Q = (int) Math.sqrt((2.0 * D * S) / H);
        Float CT = D*C + (D/q)*S +(q/2)*H;
        int invetarioMaximo =q + I;

        //Setear nuevos valores al ProveedorArticulo.

        proveedorArticulo.setLoteOptimo(Q);
        proveedorArticulo.setInventarioMaximo(invetarioMaximo);
        proveedorArticulo.setCantidadAPedir(q);
        proveedorArticulo.setCostoGeneralInventario(CT);



        //Setear nuevos valores al Articulo.

        proveedorArticulo.getArt().setStockSeguridad(stockSeguridad);

        return proveedorArticulo;

    }

    @Override
    public TipoLote getTipoLote() {
        return TipoLote.PERIODOFIJO;
    }
}
