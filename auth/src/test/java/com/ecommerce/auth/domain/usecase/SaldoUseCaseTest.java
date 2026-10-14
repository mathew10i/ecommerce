package com.ecommerce.auth.domain.usecase;

import com.ecommerce.auth.domain.exception.ReglaNegocioException;
import com.ecommerce.auth.domain.model.OperacionSaldo;
import com.ecommerce.auth.domain.model.ResultadoSaldo;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.gateway.SaldoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaldoUseCaseTest {

    private SaldoUseCase useCase;
    private EnMemoriaSaldoGateway gateway;

    @BeforeEach
    void preparar() {
        gateway = new EnMemoriaSaldoGateway();
        gateway.usuario = new Usuario(
                "usuario-1", "Ana", "ana@correo.com", null,
                "CLIENTE", 25, "3000000000", new BigDecimal("250000.00")
        );
        useCase = new SaldoUseCase(gateway);
    }

    @Test
    void debeDebitarUnaCompraUnaSolaVez() {
        ResultadoSaldo primera = useCase.debitarCompra(
                "usuario-1", new BigDecimal("170000"), "COMPRA-carrito-1"
        );
        ResultadoSaldo repetida = useCase.debitarCompra(
                "usuario-1", new BigDecimal("170000"), "COMPRA-carrito-1"
        );

        assertEquals(new BigDecimal("80000.00"), primera.saldo());
        assertEquals(new BigDecimal("80000.00"), repetida.saldo());
        assertTrue(repetida.idempotente());
    }

    @Test
    void debeRechazarSaldoInsuficienteSinModificarSaldo() {
        assertThrows(ReglaNegocioException.class, () ->
                useCase.debitarCompra(
                        "usuario-1", new BigDecimal("300000"), "COMPRA-carrito-2"
                )
        );
        assertEquals(new BigDecimal("250000.00"), gateway.usuario.getSaldo());
    }

    @Test
    void debeReintegrarUnaSolaVez() {
        useCase.debitarCompra(
                "usuario-1", new BigDecimal("170000"), "COMPRA-carrito-3"
        );
        ResultadoSaldo primera = useCase.reintegrarCompra("usuario-1", "COMPRA-carrito-3");
        ResultadoSaldo repetida = useCase.reintegrarCompra("usuario-1", "COMPRA-carrito-3");

        assertEquals(new BigDecimal("250000.00"), primera.saldo());
        assertEquals(new BigDecimal("250000.00"), repetida.saldo());
        assertTrue(repetida.idempotente());
    }

    private static class EnMemoriaSaldoGateway implements SaldoGateway {
        private Usuario usuario;
        private final Map<String, OperacionSaldo> operaciones = new HashMap<>();

        @Override
        public Usuario buscarUsuarioPorId(String usuarioId) {
            return usuario;
        }

        @Override
        public Usuario buscarUsuarioPorIdParaActualizar(String usuarioId) {
            return usuario;
        }

        @Override
        public Usuario guardarSaldo(Usuario usuario) {
            this.usuario = usuario;
            return usuario;
        }

        @Override
        public Optional<OperacionSaldo> buscarOperacionPorReferencia(String referencia) {
            return Optional.ofNullable(operaciones.get(referencia));
        }

        @Override
        public OperacionSaldo guardarOperacion(OperacionSaldo operacion) {
            operaciones.put(operacion.getReferencia(), operacion);
            return operacion;
        }
    }
}
