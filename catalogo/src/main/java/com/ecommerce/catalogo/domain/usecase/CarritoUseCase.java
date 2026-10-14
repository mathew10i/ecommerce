package com.ecommerce.catalogo.domain.usecase;

import com.ecommerce.catalogo.domain.exception.ReglaNegocioException;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.EstadoCarrito;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.gateway.AuthGateway;
import com.ecommerce.catalogo.domain.model.gateway.CarritoGateway;
import com.ecommerce.catalogo.domain.model.gateway.ProductoGateway;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@RequiredArgsConstructor
public class CarritoUseCase {

    private final CarritoGateway carritoGateway;
    private final ProductoGateway productoGateway;
    private final AuthGateway authGateway;

    public Carrito obtenerOCrearActivo(String usuarioId) {
        validarTexto(usuarioId, "usuarioId");
        return carritoGateway.buscarActivoPorUsuario(usuarioId).orElseGet(() -> {
            authGateway.consultarSaldo(usuarioId);
            Carrito nuevo = new Carrito(
                    null,
                    usuarioId,
                    EstadoCarrito.ACTIVO,
                    BigDecimal.ZERO.setScale(2),
                    LocalDateTime.now(),
                    null,
                    new ArrayList<>()
            );
            return carritoGateway.guardar(nuevo);
        });
    }

    public Carrito consultar(String carritoId) {
        return carritoGateway.buscarPorId(carritoId);
    }

    public Carrito agregarProducto(String carritoId, String productoId, Integer cantidad) {
        validarCantidad(cantidad);
        Carrito carrito = carritoGateway.buscarPorIdParaActualizar(carritoId);
        validarActivo(carrito);
        Producto producto = productoGateway.buscarPorId(productoId);

        ItemCarrito item = carrito.getItems().stream()
                .filter(actual -> actual.getProducto().getId().equals(productoId))
                .findFirst()
                .orElse(null);

        int cantidadFinal = item == null ? cantidad : item.getCantidad() + cantidad;
        validarStock(producto, cantidadFinal);

        if (item == null) {
            item = new ItemCarrito(null, producto, cantidadFinal, producto.getPrecio(), BigDecimal.ZERO);
            carrito.getItems().add(item);
        } else {
            item.setCantidad(cantidadFinal);
            item.setPrecioUnitario(producto.getPrecio());
        }

        recalcular(carrito);
        return carritoGateway.guardar(carrito);
    }

    public Carrito cambiarCantidad(String carritoId, String productoId, Integer cantidad) {
        validarCantidad(cantidad);
        Carrito carrito = carritoGateway.buscarPorIdParaActualizar(carritoId);
        validarActivo(carrito);
        ItemCarrito item = buscarItem(carrito, productoId);
        Producto producto = productoGateway.buscarPorId(productoId);
        validarStock(producto, cantidad);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(producto.getPrecio());
        recalcular(carrito);
        return carritoGateway.guardar(carrito);
    }

    public Carrito retirarProducto(String carritoId, String productoId) {
        Carrito carrito = carritoGateway.buscarPorIdParaActualizar(carritoId);
        validarActivo(carrito);
        ItemCarrito item = buscarItem(carrito, productoId);
        carrito.getItems().remove(item);
        recalcular(carrito);
        return carritoGateway.guardar(carrito);
    }

    private ItemCarrito buscarItem(Carrito carrito, String productoId) {
        return carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException("El producto no está en el carrito"));
    }

    private void recalcular(Carrito carrito) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrito item : carrito.getItems()) {
            BigDecimal subtotal = item.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));
            item.setSubtotal(subtotal);
            total = total.add(subtotal);
        }
        carrito.setTotal(total);
    }

    private void validarActivo(Carrito carrito) {
        if (carrito.getEstado() != EstadoCarrito.ACTIVO) {
            throw new ReglaNegocioException("El carrito ya fue comprado y no puede modificarse");
        }
    }

    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
        }
    }

    private void validarStock(Producto producto, int cantidad) {
        if (cantidad > producto.getStock()) {
            throw new ReglaNegocioException(
                    "Stock insuficiente para el producto " + producto.getNombre()
            );
        }
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio");
        }
    }
}
