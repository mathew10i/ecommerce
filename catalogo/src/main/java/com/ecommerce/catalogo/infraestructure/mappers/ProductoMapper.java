package com.ecommerce.catalogo.infraestructure.mappers;

import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.infraestructure.driver_adapter.ProductoData;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public Producto toDomain(ProductoData data) {
        return new Producto(
                data.getId(),
                data.getNombre(),
                data.getDescripcion(),
                data.getPrecio(),
                data.getStock(),
                data.getImagenUrl()
        );
    }

    public ProductoData toData(Producto producto, Long version) {
        return new ProductoData(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getImagenUrl(),
                version
        );
    }
}
