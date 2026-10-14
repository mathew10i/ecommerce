package com.ecommerce.catalogo.domain.usecase;

import com.ecommerce.catalogo.domain.exception.ReglaNegocioException;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.EstadoCarrito;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.gateway.CarritoGateway;
import com.ecommerce.catalogo.domain.model.gateway.ProductoGateway;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompraUseCaseTest {

    @Test
    void debeCalcularTotalYDescontarInventario() {
        Producto mouse = producto("mouse", "Mouse", "45000", 5);
        Producto audifonos = producto("audifonos", "Audífonos", "80000", 3);
        Carrito carrito = carrito(mouse, audifonos);
        CompraUseCase useCase = new CompraUseCase(
                new EnMemoriaCarritoGateway(carrito),
                new EnMemoriaProductoGateway(mouse, audifonos)
        );

        Carrito confirmado = useCase.confirmar(useCase.preparar("carrito-1"));

        assertEquals(new BigDecimal("170000"), confirmado.getTotal());
        assertEquals(EstadoCarrito.COMPRADO, confirmado.getEstado());
        assertEquals(3, mouse.getStock());
        assertEquals(2, audifonos.getStock());
    }

    @Test
    void debeRechazarCompraSinStockSinCambiarInventario() {
        Producto mouse = producto("mouse", "Mouse", "45000", 1);
        Carrito carrito = new Carrito(
                "carrito-1", "usuario-1", EstadoCarrito.ACTIVO, BigDecimal.ZERO,
                LocalDateTime.now(), null,
                new ArrayList<>(List.of(new ItemCarrito(null, mouse, 2, mouse.getPrecio(), BigDecimal.ZERO)))
        );
        CompraUseCase useCase = new CompraUseCase(
                new EnMemoriaCarritoGateway(carrito),
                new EnMemoriaProductoGateway(mouse)
        );

        assertThrows(ReglaNegocioException.class, () -> useCase.preparar("carrito-1"));
        assertEquals(1, mouse.getStock());
    }

    private Producto producto(String id, String nombre, String precio, int stock) {
        return new Producto(id, nombre, null, new BigDecimal(precio), stock, null);
    }

    private Carrito carrito(Producto mouse, Producto audifonos) {
        return new Carrito(
                "carrito-1", "usuario-1", EstadoCarrito.ACTIVO, BigDecimal.ZERO,
                LocalDateTime.now(), null,
                new ArrayList<>(List.of(
                        new ItemCarrito(null, mouse, 2, mouse.getPrecio(), BigDecimal.ZERO),
                        new ItemCarrito(null, audifonos, 1, audifonos.getPrecio(), BigDecimal.ZERO)
                ))
        );
    }

    private record EnMemoriaCarritoGateway(Carrito carrito) implements CarritoGateway {
        @Override public Carrito guardar(Carrito carrito) { return carrito; }
        @Override public Carrito buscarPorId(String id) { return carrito; }
        @Override public Carrito buscarPorIdParaActualizar(String id) { return carrito; }
        @Override public Optional<Carrito> buscarActivoPorUsuario(String id) { return Optional.of(carrito); }
    }

    private static class EnMemoriaProductoGateway implements ProductoGateway {
        private final List<Producto> productos;
        EnMemoriaProductoGateway(Producto... productos) { this.productos = List.of(productos); }
        @Override public Producto guardar(Producto producto) { return producto; }
        @Override public Producto buscarPorId(String id) { return buscarPorIdParaActualizar(id); }
        @Override public Producto buscarPorIdParaActualizar(String id) {
            return productos.stream().filter(p -> p.getId().equals(id)).findFirst().orElseThrow();
        }
        @Override public List<Producto> listar() { return productos; }
        @Override public void eliminar(String id) { }
    }
}
