package com.ecommerce.catalogo.infraestructure.driver_adapter;

import com.ecommerce.catalogo.domain.model.EstadoCarrito;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CarritoDataJpaRepository extends JpaRepository<CarritoData, String> {

    @EntityGraph(attributePaths = {"items", "items.producto"})
    Optional<CarritoData> findByUsuarioIdAndEstado(String usuarioId, EstadoCarrito estado);

    @EntityGraph(attributePaths = {"items", "items.producto"})
    @Query("select c from CarritoData c where c.id = :id")
    Optional<CarritoData> findByIdWithItems(@Param("id") String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CarritoData c where c.id = :id")
    Optional<CarritoData> findByIdForUpdate(@Param("id") String id);
}
