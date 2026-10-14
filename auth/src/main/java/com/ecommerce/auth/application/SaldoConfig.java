package com.ecommerce.auth.application;

import com.ecommerce.auth.domain.model.gateway.SaldoGateway;
import com.ecommerce.auth.domain.usecase.SaldoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaldoConfig {

    @Bean
    public SaldoUseCase saldoUseCase(SaldoGateway saldoGateway) {
        return new SaldoUseCase(saldoGateway);
    }
}
