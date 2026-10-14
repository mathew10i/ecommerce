package com.ecommerce.catalogo.domain.model.gateway;

import com.ecommerce.catalogo.domain.model.Producto;

import java.util.List;

public interface ProductoGateway {
    Producto guardar(Producto producto);

    Producto buscarPorId(String productoId);

    Producto buscarPorIdParaActualizar(String productoId);

    List<Producto> listar();

    void eliminar(String productoId);
}
