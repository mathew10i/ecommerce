package com.ecommerce.auth.domain.model.gateway;

import com.ecommerce.auth.domain.model.OperacionSaldo;
import com.ecommerce.auth.domain.model.Usuario;

import java.util.Optional;

public interface SaldoGateway {
    Usuario buscarUsuarioPorId(String usuarioId);

    Usuario buscarUsuarioPorIdParaActualizar(String usuarioId);

    Usuario guardarSaldo(Usuario usuario);

    Optional<OperacionSaldo> buscarOperacionPorReferencia(String referencia);

    OperacionSaldo guardarOperacion(OperacionSaldo operacion);
}
