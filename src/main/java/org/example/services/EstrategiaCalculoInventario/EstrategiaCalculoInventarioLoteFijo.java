package org.example.services.EstrategiaCalculoInventario;
import org.example.entity.ProveedorArticulo;
import org.example.enums.TipoLote;
import org.springframework.stereotype.Service;

@Service
public class EstrategiaCalculoInventarioLoteFijo implements EstrategiaCalculoInventario {
    @Override
    public ProveedorArticulo calcular(ProveedorArticulo proveedorArticulo) {

        // Lote Fijo.
        // Variables para el cálculo del lote óptimo y punto de pedido

        int d = proveedorArticulo.getArt().getDemandaDiaria(); // Demanda diaria
        int D = d * 365; // Demanda anual
        int L = proveedorArticulo.getDemoraEntrega(); // Demora de entrega
        double z = proveedorArticulo.getNivelDeServicio() == 85 ? 1.036 : 1.645; // Nivel de servicio (número de desvíos estándar)
        int o = proveedorArticulo.getArt().getDesviacionEstandar(); // Desviación estándar

        // Calcular stock de seguridad y punto de pedido

        int stockSeguridad = (int) Math.round(z * o * Math.sqrt(L)); // Stock de seguridad
        int R = (int) Math.round(d * L + stockSeguridad); // Punto de pedido

        // Calcular lote óptimo y costo total

        float C = proveedorArticulo.getCostoUnitario(); // Costo de pedido
        float H = proveedorArticulo.getCostoMantenimiento(); // Costo de mantenimiento
        float S = proveedorArticulo.getCostoPedido(); // Costo de pedido
        int Q = (int) Math.round(Math.sqrt((2.0 * D * S) / H)); // Lote óptimo
        float CT = Math.round(D * C + (D / (float) Q) * S + (Q / 2.0f) * H); // Costo total redondeado

        // Setear nuevos valores al ProveedorArticulo

        proveedorArticulo.setPuntoPedido(R);
        proveedorArticulo.setLoteOptimo(Q);
        proveedorArticulo.setCostoGeneralInventario(CT);

        // Setear stock de seguridad al Articulo

        proveedorArticulo.getArt().setStockSeguridad(stockSeguridad);

        return proveedorArticulo;
    }

    @Override
    public TipoLote getTipoLote() {
        return TipoLote.LOTEFIJO;
    }
}
