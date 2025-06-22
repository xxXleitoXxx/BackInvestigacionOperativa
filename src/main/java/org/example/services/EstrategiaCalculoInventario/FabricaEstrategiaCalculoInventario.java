package org.example.services.EstrategiaCalculoInventario;
import org.example.enums.TipoLote;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FabricaEstrategiaCalculoInventario {

    private final Map<TipoLote, EstrategiaCalculoInventario> estrategias = new HashMap<>();

    public FabricaEstrategiaCalculoInventario(List<EstrategiaCalculoInventario> strategyList) {
        for (EstrategiaCalculoInventario estrategia : strategyList) {
            estrategias.put(estrategia.getTipoLote(), estrategia);
        }
    }

    public EstrategiaCalculoInventario obtener(TipoLote tipoLote) {
        EstrategiaCalculoInventario estrategia = estrategias.get(tipoLote);
        if (estrategia == null) {
            throw new IllegalArgumentException("No existe estrategia para el tipo: " + tipoLote);
        }
        return estrategia;
    }

}
