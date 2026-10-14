package com.ecommerce.catalogo.infraestructure.entry_points.dto;

import com.ecommerce.catalogo.domain.model.Producto;

import java.math.BigDecimal;

public record ProductoRequest(
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        String imagenUrl
) {
    public Producto toDomain() {
        return new Producto(null, nombre, descripcion, precio, stock, imagenUrl);
    }
}
