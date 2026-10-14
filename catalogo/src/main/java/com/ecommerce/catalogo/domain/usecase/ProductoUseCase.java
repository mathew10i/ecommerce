package com.ecommerce.catalogo.domain.usecase;

import com.ecommerce.catalogo.domain.exception.ReglaNegocioException;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.gateway.ProductoGateway;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
public class ProductoUseCase {

    private final ProductoGateway productoGateway;

    public Producto crear(Producto producto) {
        producto.setId(null);
        validar(producto);
        producto.setPrecio(normalizar(producto.getPrecio()));
        return productoGateway.guardar(producto);
    }

    public List<Producto> listar() {
        return productoGateway.listar();
    }

    public Producto consultar(String productoId) {
        return productoGateway.buscarPorId(productoId);
    }

    public Producto actualizar(String productoId, Producto cambios) {
        Producto actual = productoGateway.buscarPorId(productoId);
        if (cambios.getNombre() != null) actual.setNombre(cambios.getNombre());
        if (cambios.getDescripcion() != null) actual.setDescripcion(cambios.getDescripcion());
        if (cambios.getPrecio() != null) actual.setPrecio(cambios.getPrecio());
        if (cambios.getStock() != null) actual.setStock(cambios.getStock());
        if (cambios.getImagenUrl() != null) actual.setImagenUrl(cambios.getImagenUrl());
        validar(actual);
        actual.setPrecio(normalizar(actual.getPrecio()));
        return productoGateway.guardar(actual);
    }

    public void eliminar(String productoId) {
        productoGateway.buscarPorId(productoId);
        productoGateway.eliminar(productoId);
    }

    private void validar(Producto producto) {
        validarTexto(producto.getNombre(), "nombre");
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El precio debe ser mayor que cero");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new ReglaNegocioException("El stock no puede ser negativo");
        }
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio");
        }
    }

    private BigDecimal normalizar(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
