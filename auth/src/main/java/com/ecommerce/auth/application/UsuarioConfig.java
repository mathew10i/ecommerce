package com.ecommerce.auth.application;

import com.ecommerce.auth.domain.model.gateway.EncrypterGateway;
import com.ecommerce.auth.domain.model.gateway.UsuarioGetaway;
import com.ecommerce.auth.domain.usecase.UsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsuarioConfig {
    @Bean

    public UsuarioUseCase usuarioUseCase(
                UsuarioGetaway usuarioGetaway,
                EncrypterGateway encrypterGateway
        ) {

            return new UsuarioUseCase(
                    usuarioGetaway,
                    encrypterGateway
            );
        }
}
