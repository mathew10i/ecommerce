package com.ecommerce.catalogo.domain.usecase;

import com.ecommerce.catalogo.domain.exception.ReglaNegocioException;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.gateway.ProductoGateway;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductoUseCaseTest {

    @Test
    void debeCrearProductoConPrecioValido() {
        EnMemoriaProductoGateway gateway = new EnMemoriaProductoGateway();
        ProductoUseCase useCase = new ProductoUseCase(gateway);

        Producto creado = useCase.crear(new Producto(
                null, "Mouse", "Inalámbrico", new BigDecimal("45000"), 10, "https://img"
        ));

        assertEquals(new BigDecimal("45000.00"), creado.getPrecio());
        assertEquals(10, creado.getStock());
    }

    @Test
    void debeRechazarPrecioCeroYStockNegativo() {
        ProductoUseCase useCase = new ProductoUseCase(new EnMemoriaProductoGateway());

        assertThrows(ReglaNegocioException.class, () -> useCase.crear(
                new Producto(null, "Mouse", null, BigDecimal.ZERO, -1, null)
        ));
    }

    private static class EnMemoriaProductoGateway implements ProductoGateway {
        private final List<Producto> productos = new ArrayList<>();

        @Override
        public Producto guardar(Producto producto) {
            if (producto.getId() == null) producto.setId("producto-1");
            productos.add(producto);
            return producto;
        }

        @Override public Producto buscarPorId(String id) { return productos.getFirst(); }
        @Override public Producto buscarPorIdParaActualizar(String id) { return buscarPorId(id); }
        @Override public List<Producto> listar() { return productos; }
        @Override public void eliminar(String id) { productos.clear(); }
    }
}
