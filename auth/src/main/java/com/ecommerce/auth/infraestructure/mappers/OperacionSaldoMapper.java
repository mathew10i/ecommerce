package com.ecommerce.auth.infraestructure.mappers;

import com.ecommerce.auth.domain.model.OperacionSaldo;
import com.ecommerce.auth.infraestructure.driver_adapter.OperacionSaldoData;
import org.springframework.stereotype.Component;

@Component
public class OperacionSaldoMapper {

    public OperacionSaldo toDomain(OperacionSaldoData data) {
        return new OperacionSaldo(
                data.getIdOperacion(),
                data.getReferencia(),
                data.getUsuarioId(),
                data.getMonto(),
                data.getTipo(),
                data.getEstado(),
                data.getFechaActualizacion()
        );
    }

    public OperacionSaldoData toData(OperacionSaldo operacion) {
        return new OperacionSaldoData(
                operacion.getIdOperacion(),
                operacion.getReferencia(),
                operacion.getUsuarioId(),
                operacion.getMonto(),
                operacion.getTipo(),
                operacion.getEstado(),
                operacion.getFechaActualizacion()
        );
    }
}
