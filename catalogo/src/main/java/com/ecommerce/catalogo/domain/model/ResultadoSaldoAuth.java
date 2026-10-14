package com.ecommerce.catalogo.domain.model;

import java.math.BigDecimal;

public record ResultadoSaldoAuth(
        String usuarioId,
        BigDecimal saldo,
        String referencia,
        boolean idempotente
) {
}
