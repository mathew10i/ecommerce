package com.ecommerce.catalogo.infraestructure.mappers;

import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.infraestructure.driver_adapter.CarritoData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class CarritoMapper {

    private final ProductoMapper productoMapper;

    public Carrito toDomain(CarritoData data) {
        var items = data.getItems() == null
                ? new ArrayList<ItemCarrito>()
                : data.getItems().stream()
                .map(item -> new ItemCarrito(
                        item.getId(),
                        productoMapper.toDomain(item.getProducto()),
                        item.getCantidad(),
                        item.getPrecioUnitario(),
                        item.getSubtotal()
                ))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        return new Carrito(
                data.getId(),
                data.getUsuarioId(),
                data.getEstado(),
                data.getTotal(),
                data.getFechaCreacion(),
                data.getFechaCompra(),
                items
        );
    }
}
