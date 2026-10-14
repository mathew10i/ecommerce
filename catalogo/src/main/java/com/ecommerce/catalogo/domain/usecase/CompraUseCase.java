package com.ecommerce.catalogo.domain.usecase;

import com.ecommerce.catalogo.domain.exception.ConflictoException;
import com.ecommerce.catalogo.domain.exception.ReglaNegocioException;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.CompraPreparada;
import com.ecommerce.catalogo.domain.model.EstadoCarrito;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.gateway.CarritoGateway;
import com.ecommerce.catalogo.domain.model.gateway.ProductoGateway;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CompraUseCase {

    private final CarritoGateway carritoGateway;
    private final ProductoGateway productoGateway;

    public CompraPreparada preparar(String carritoId) {
        Carrito carrito = carritoGateway.buscarPorIdParaActualizar(carritoId);
        if (carrito.getEstado() == EstadoCarrito.COMPRADO) {
            throw new ConflictoException("El carrito ya fue comprado; no se realizará otro cobro");
        }
        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new ReglaNegocioException("No se puede comprar un carrito vacío");
        }

        List<Producto> productos = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = productoGateway.buscarPorIdParaActualizar(item.getProducto().getId());
            if (item.getCantidad() <= 0) {
                throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
            }
            if (producto.getStock() < item.getCantidad()) {
                throw new ReglaNegocioException(
                        "Stock insuficiente para el producto " + producto.getNombre()
                );
            }
            item.setProducto(producto);
            item.setPrecioUnitario(producto.getPrecio());
            item.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
            total = total.add(item.getSubtotal());
            productos.add(producto);
        }
        carrito.setTotal(total);
        return new CompraPreparada(carrito, productos, total, "COMPRA-" + carrito.getId());
    }

    public Carrito confirmar(CompraPreparada preparada) {
        Carrito carrito = preparada.carrito();
        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = preparada.productosBloqueados().stream()
                    .filter(actual -> actual.getId().equals(item.getProducto().getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Producto bloqueado no encontrado"));
            producto.setStock(producto.getStock() - item.getCantidad());
            productoGateway.guardar(producto);
        }
        carrito.setEstado(EstadoCarrito.COMPRADO);
        carrito.setTotal(preparada.total());
        carrito.setFechaCompra(LocalDateTime.now());
        return carritoGateway.guardar(carrito);
    }
}
