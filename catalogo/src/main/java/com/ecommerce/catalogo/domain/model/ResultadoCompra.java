package com.ecommerce.catalogo.domain.model;

import java.math.BigDecimal;

public record ResultadoCompra(
        String carritoId,
        String usuarioId,
        String estado,
        BigDecimal total,
        BigDecimal saldoRestante,
        String referencia
) {
}
