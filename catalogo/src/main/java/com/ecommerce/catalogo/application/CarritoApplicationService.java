package com.ecommerce.catalogo.application;

import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.usecase.CarritoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarritoApplicationService {

    private final CarritoUseCase useCase;

    @Transactional
    public Carrito obtenerOCrearActivo(String usuarioId) {
        return useCase.obtenerOCrearActivo(usuarioId);
    }

    @Transactional(readOnly = true)
    public Carrito consultar(String carritoId) {
        return useCase.consultar(carritoId);
    }

    @Transactional
    public Carrito agregarProducto(String carritoId, String productoId, Integer cantidad) {
        return useCase.agregarProducto(carritoId, productoId, cantidad);
    }

    @Transactional
    public Carrito cambiarCantidad(String carritoId, String productoId, Integer cantidad) {
        return useCase.cambiarCantidad(carritoId, productoId, cantidad);
    }

    @Transactional
    public Carrito retirarProducto(String carritoId, String productoId) {
        return useCase.retirarProducto(carritoId, productoId);
    }
}
