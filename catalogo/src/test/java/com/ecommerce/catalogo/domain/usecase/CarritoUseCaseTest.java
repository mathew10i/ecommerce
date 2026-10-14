package com.ecommerce.catalogo.domain.usecase;

import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.EstadoCarrito;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.ResultadoSaldoAuth;
import com.ecommerce.catalogo.domain.model.gateway.AuthGateway;
import com.ecommerce.catalogo.domain.model.gateway.CarritoGateway;
import com.ecommerce.catalogo.domain.model.gateway.ProductoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarritoUseCaseTest {

    private CarritoUseCase useCase;
    private EnMemoriaCarritoGateway carritos;

    @BeforeEach
    void preparar() {
        Producto producto = new Producto(
                "producto-1", "Mouse", "Inalámbrico", new BigDecimal("45000.00"), 10, null
        );
        carritos = new EnMemoriaCarritoGateway();
        useCase = new CarritoUseCase(
                carritos,
                new EnMemoriaProductoGateway(producto),
                new AuthGatewayFalso()
        );
    }

    @Test
    void agregarElMismoProductoDebeAcumularCantidadSinDuplicarlo() {
        Carrito carrito = useCase.obtenerOCrearActivo("usuario-1");
        useCase.agregarProducto(carrito.getId(), "producto-1", 1);
        Carrito actualizado = useCase.agregarProducto(carrito.getId(), "producto-1", 2);

        assertEquals(1, actualizado.getItems().size());
        assertEquals(3, actualizado.getItems().getFirst().getCantidad());
        assertEquals(new BigDecimal("135000.00"), actualizado.getTotal());
    }

    private static class EnMemoriaCarritoGateway implements CarritoGateway {
        private Carrito carrito;

        @Override
        public Carrito guardar(Carrito carrito) {
            if (carrito.getId() == null) carrito.setId("carrito-1");
            this.carrito = carrito;
            return carrito;
        }

        @Override public Carrito buscarPorId(String id) { return carrito; }
        @Override public Carrito buscarPorIdParaActualizar(String id) { return carrito; }
        @Override public Optional<Carrito> buscarActivoPorUsuario(String id) {
            return Optional.ofNullable(carrito)
                    .filter(c -> c.getEstado() == EstadoCarrito.ACTIVO);
        }
    }

    private record EnMemoriaProductoGateway(Producto producto) implements ProductoGateway {
        @Override public Producto guardar(Producto producto) { return producto; }
        @Override public Producto buscarPorId(String id) { return producto; }
        @Override public Producto buscarPorIdParaActualizar(String id) { return producto; }
        @Override public List<Producto> listar() { return List.of(producto); }
        @Override public void eliminar(String id) { }
    }

    private static class AuthGatewayFalso implements AuthGateway {
        @Override public ResultadoSaldoAuth consultarSaldo(String id) {
            return new ResultadoSaldoAuth(id, BigDecimal.ZERO, null, false);
        }
        @Override public ResultadoSaldoAuth debitar(String id, BigDecimal monto, String ref) {
            throw new UnsupportedOperationException();
        }
        @Override public ResultadoSaldoAuth reintegrar(String id, String ref) {
            throw new UnsupportedOperationException();
        }
    }
}
