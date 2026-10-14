package com.ecommerce.catalogo.infraestructure.driver_adapter;

import com.ecommerce.catalogo.domain.exception.ConflictoException;
import com.ecommerce.catalogo.domain.exception.RecursoNoEncontradoException;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.gateway.ProductoGateway;
import com.ecommerce.catalogo.infraestructure.mappers.ProductoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductoDataGatewayImpl implements ProductoGateway {

    private final ProductoDataJpaRepository repository;
    private final ProductoMapper mapper;

    @Override
    public Producto guardar(Producto producto) {
        Long version = producto.getId() == null
                ? null
                : repository.findById(producto.getId()).map(ProductoData::getVersion).orElse(null);
        return mapper.toDomain(repository.save(mapper.toData(producto, version)));
    }

    @Override
    public Producto buscarPorId(String productoId) {
        return repository.findById(productoId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
    }

    @Override
    public Producto buscarPorIdParaActualizar(String productoId) {
        return repository.findByIdForUpdate(productoId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
    }

    @Override
    public List<Producto> listar() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void eliminar(String productoId) {
        try {
            repository.deleteById(productoId);
            repository.flush();
        } catch (DataIntegrityViolationException error) {
            throw new ConflictoException(
                    "No se puede eliminar un producto que pertenece a un carrito"
            );
        }
    }
}
