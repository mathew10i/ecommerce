package com.ecommerce.catalogo.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record CompraPreparada(
        Carrito carrito,
        List<Producto> productosBloqueados,
        BigDecimal total,
        String referencia
) {
}
