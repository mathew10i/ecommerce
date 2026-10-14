package com.ecommerce.catalogo.domain.model.gateway;

import com.ecommerce.catalogo.domain.model.Carrito;

import java.util.Optional;

public interface CarritoGateway {
    Carrito guardar(Carrito carrito);

    Carrito buscarPorId(String carritoId);

    Carrito buscarPorIdParaActualizar(String carritoId);

    Optional<Carrito> buscarActivoPorUsuario(String usuarioId);
}
