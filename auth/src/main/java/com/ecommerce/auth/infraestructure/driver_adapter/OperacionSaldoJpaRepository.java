package com.ecommerce.auth.infraestructure.driver_adapter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperacionSaldoJpaRepository extends JpaRepository<OperacionSaldoData, String> {
    Optional<OperacionSaldoData> findByReferencia(String referencia);
}
