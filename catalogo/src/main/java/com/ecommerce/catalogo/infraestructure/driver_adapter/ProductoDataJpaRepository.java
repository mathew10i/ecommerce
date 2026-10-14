package com.ecommerce.catalogo.infraestructure.driver_adapter;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductoDataJpaRepository extends JpaRepository<ProductoData, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductoData p where p.id = :id")
    Optional<ProductoData> findByIdForUpdate(@Param("id") String id);
}
