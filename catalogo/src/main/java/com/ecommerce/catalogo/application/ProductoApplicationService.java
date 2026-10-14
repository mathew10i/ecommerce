package com.ecommerce.catalogo.application;

import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.usecase.ProductoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoApplicationService {

    private final ProductoUseCase useCase;

    @Transactional
    public Producto crear(Producto producto) {
        return useCase.crear(producto);
    }

    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return useCase.listar();
    }

    @Transactional(readOnly = true)
    public Producto consultar(String id) {
        return useCase.consultar(id);
    }

    @Transactional
    public Producto actualizar(String id, Producto producto) {
        return useCase.actualizar(id, producto);
    }

    @Transactional
    public void eliminar(String id) {
        useCase.eliminar(id);
    }
}
