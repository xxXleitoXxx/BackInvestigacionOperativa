package org.example.services.EstrategiaCalculoInventario;
import org.example.entity.ProveedorArticulo;
import org.example.enums.TipoLote;
import org.springframework.stereotype.Service;

@Service
public class EstrategiaCalculoInventarioPeriodoFijo implements EstrategiaCalculoInventario {

    @Override
    public ProveedorArticulo calcular(ProveedorArticulo proveedorArticulo) {

        //Periodo Fijo.
        //Variables para cálculo del lote óptimo o punto para volver a pedir R.

        int d = proveedorArticulo.getArt().getDemandaDiaria(); //Demanda diaria
        int D = d * 365; //Demanda anual
        int T = proveedorArticulo.getPeriodoRevision(); //Periodo revision
        int L = proveedorArticulo.getDemoraEntrega(); //Demora de entrega
        double z; //Números de desvios estandar respecto a la media.
        if(proveedorArticulo.getNivelDeServicio() == 85){ z = 1.036;} else { z=1.645;}
        int o = proveedorArticulo.getArt().getDesviacionEstandar(); //Desvio Estandar.
        int I = proveedorArticulo.getArt().getStock(); //Stock Actual.

        //Calcular Stock de seguridad y cantidad a pedir q

        int stockSeguridad = (int) Math.round(z * o * (T + L) * Math.sqrt(T+L)); //Stock de seguridad
        int q = (int) Math.round(d * (T + L) + stockSeguridad - I);

        //Calcular cantidadOptima e inventario maximo.

        Float C = proveedorArticulo.getCostoUnitario(); //Costo por Unidad
        Float H = proveedorArticulo.getCostoMantenimiento(); //Costo de mantenimiento
        Float S = proveedorArticulo.getCostoPedido();   //Costo de pedido
        int Q = (int) Math.round(Math.sqrt((2.0 * D * S) / H));
        Float CT = (float) Math.round(D * C + (D / (float) q) * S + (q / 2.0f) * H);
        int invetarioMaximo = q + I;

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
