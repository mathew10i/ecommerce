package com.ecommerce.auth.domain.model;

import java.math.BigDecimal;

public record ResultadoSaldo(
        String usuarioId,
        BigDecimal saldo,
        String referencia,
        String tipo,
        String estado,
        boolean idempotente
) {
}
