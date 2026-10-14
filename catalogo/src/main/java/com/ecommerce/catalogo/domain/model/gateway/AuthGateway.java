package com.ecommerce.catalogo.domain.model.gateway;

import com.ecommerce.catalogo.domain.model.ResultadoSaldoAuth;

import java.math.BigDecimal;

public interface AuthGateway {
    ResultadoSaldoAuth consultarSaldo(String usuarioId);

    ResultadoSaldoAuth debitar(String usuarioId, BigDecimal monto, String referencia);

    ResultadoSaldoAuth reintegrar(String usuarioId, String referencia);
}
