package com.ecommerce.catalogo.application;

import com.ecommerce.catalogo.domain.model.gateway.AuthGateway;
import com.ecommerce.catalogo.domain.model.gateway.CarritoGateway;
import com.ecommerce.catalogo.domain.model.gateway.ProductoGateway;
import com.ecommerce.catalogo.domain.usecase.CarritoUseCase;
import com.ecommerce.catalogo.domain.usecase.CompraUseCase;
import com.ecommerce.catalogo.domain.usecase.ProductoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogoConfig {

    @Bean
    public ProductoUseCase productoUseCase(ProductoGateway productoGateway) {
        return new ProductoUseCase(productoGateway);
    }

    @Bean
    public CarritoUseCase carritoUseCase(
            CarritoGateway carritoGateway,
            ProductoGateway productoGateway,
            AuthGateway authGateway
    ) {
        return new CarritoUseCase(carritoGateway, productoGateway, authGateway);
    }

    @Bean
    public CompraUseCase compraUseCase(
            CarritoGateway carritoGateway,
            ProductoGateway productoGateway
    ) {
        return new CompraUseCase(carritoGateway, productoGateway);
    }
}
