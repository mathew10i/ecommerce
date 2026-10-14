package com.ecommerce.catalogo.infraestructure.driver_adapter;

import com.ecommerce.catalogo.domain.exception.RecursoNoEncontradoException;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.EstadoCarrito;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.model.gateway.CarritoGateway;
import com.ecommerce.catalogo.infraestructure.mappers.CarritoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CarritoDataGatewayImpl implements CarritoGateway {

    private final CarritoDataJpaRepository carritoRepository;
    private final ProductoDataJpaRepository productoRepository;
    private final CarritoMapper mapper;

    @Override
    public Carrito guardar(Carrito carrito) {
        CarritoData data = carrito.getId() == null
                ? new CarritoData()
                : carritoRepository.findByIdWithItems(carrito.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrito no encontrado"));

        data.setUsuarioId(carrito.getUsuarioId());
        data.setEstado(carrito.getEstado());
        data.setCarritoActivo(carrito.getEstado() == EstadoCarrito.ACTIVO ? Boolean.TRUE : null);
        data.setTotal(carrito.getTotal());
        data.setFechaCreacion(carrito.getFechaCreacion());
        data.setFechaCompra(carrito.getFechaCompra());

        Set<String> productosDeseados = new HashSet<>();
        for (ItemCarrito item : carrito.getItems()) {
            productosDeseados.add(item.getProducto().getId());
        }
        data.getItems().removeIf(
                existente -> !productosDeseados.contains(existente.getProducto().getId())
        );

        for (ItemCarrito item : carrito.getItems()) {
            ProductoData producto = productoRepository.findById(item.getProducto().getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
            ItemCarritoData itemData = data.getItems().stream()
                    .filter(existente -> existente.getProducto().getId().equals(producto.getId()))
                    .findFirst()
                    .orElseGet(() -> {
                        ItemCarritoData nuevo = new ItemCarritoData();
                        nuevo.setCarrito(data);
                        data.getItems().add(nuevo);
                        return nuevo;
                    });
            itemData.setProducto(producto);
            itemData.setCantidad(item.getCantidad());
            itemData.setPrecioUnitario(item.getPrecioUnitario());
            itemData.setSubtotal(item.getSubtotal());
        }

        return mapper.toDomain(carritoRepository.save(data));
    }

    @Override
    public Carrito buscarPorId(String carritoId) {
        return carritoRepository.findByIdWithItems(carritoId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrito no encontrado"));
    }

    @Override
    public Carrito buscarPorIdParaActualizar(String carritoId) {
        CarritoData data = carritoRepository.findByIdForUpdate(carritoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrito no encontrado"));
        data.getItems().forEach(item -> item.getProducto().getId());
        return mapper.toDomain(data);
    }

    @Override
    public Optional<Carrito> buscarActivoPorUsuario(String usuarioId) {
        return carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO)
                .map(mapper::toDomain);
    }
}
