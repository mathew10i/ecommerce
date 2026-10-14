package com.ecommerce.auth.application;

import com.ecommerce.auth.domain.model.ResultadoSaldo;
import com.ecommerce.auth.domain.usecase.SaldoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SaldoApplicationService {

    private final SaldoUseCase saldoUseCase;

    @Transactional(readOnly = true)
    public ResultadoSaldo consultar(String usuarioId) {
        return saldoUseCase.consultar(usuarioId);
    }

    @Transactional
    public ResultadoSaldo recargar(String usuarioId, BigDecimal monto, String referencia) {
        return saldoUseCase.recargar(usuarioId, monto, referencia);
    }

    @Transactional
    public ResultadoSaldo debitarCompra(String usuarioId, BigDecimal monto, String referencia) {
        return saldoUseCase.debitarCompra(usuarioId, monto, referencia);
    }

    @Transactional
    public ResultadoSaldo reintegrarCompra(String usuarioId, String referencia) {
        return saldoUseCase.reintegrarCompra(usuarioId, referencia);
    }
}
