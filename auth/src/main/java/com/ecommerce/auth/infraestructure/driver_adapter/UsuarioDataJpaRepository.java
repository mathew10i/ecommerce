package com.ecommerce.auth.infraestructure.driver_adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface UsuarioDataJpaRepository extends JpaRepository<UsuarioData,String> {
    boolean existsByCorreo(String correo);
    Optional<UsuarioData> findByCorreo(String correo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UsuarioData u where u.idUsuario = :idUsuario")
    Optional<UsuarioData> findByIdForUpdate(@Param("idUsuario") String idUsuario);
}
