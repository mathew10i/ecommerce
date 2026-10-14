package com.ecommerce.auth.domain.usecase;

import com.ecommerce.auth.domain.exception.ConflictoException;
import com.ecommerce.auth.domain.exception.ReglaNegocioException;
import com.ecommerce.auth.domain.model.EstadoOperacionSaldo;
import com.ecommerce.auth.domain.model.OperacionSaldo;
import com.ecommerce.auth.domain.model.ResultadoSaldo;
import com.ecommerce.auth.domain.model.TipoOperacionSaldo;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.gateway.SaldoGateway;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class SaldoUseCase {

    private final SaldoGateway saldoGateway;

    public ResultadoSaldo consultar(String usuarioId) {
        validarTexto(usuarioId, "usuarioId");
        Usuario usuario = saldoGateway.buscarUsuarioPorId(usuarioId);
        return respuesta(usuario, null, false);
    }

    public ResultadoSaldo recargar(String usuarioId, BigDecimal monto, String referencia) {
        validarEntrada(usuarioId, monto, referencia);
        Usuario usuario = saldoGateway.buscarUsuarioPorIdParaActualizar(usuarioId);
        Optional<OperacionSaldo> existente = saldoGateway.buscarOperacionPorReferencia(referencia);

        if (existente.isPresent()) {
            validarMismaOperacion(existente.get(), usuarioId, monto, TipoOperacionSaldo.RECARGA);
            return respuesta(usuario, existente.get(), true);
        }

        usuario.setSaldo(saldoActual(usuario).add(normalizar(monto)));
        Usuario actualizado = saldoGateway.guardarSaldo(usuario);
        OperacionSaldo operacion = nuevaOperacion(
                usuarioId, monto, referencia, TipoOperacionSaldo.RECARGA
        );
        saldoGateway.guardarOperacion(operacion);
        return respuesta(actualizado, operacion, false);
    }

    public ResultadoSaldo debitarCompra(String usuarioId, BigDecimal monto, String referencia) {
        validarEntrada(usuarioId, monto, referencia);
        Usuario usuario = saldoGateway.buscarUsuarioPorIdParaActualizar(usuarioId);
        Optional<OperacionSaldo> existente = saldoGateway.buscarOperacionPorReferencia(referencia);

        if (existente.isPresent()) {
            OperacionSaldo operacion = existente.get();
            validarMismaOperacion(operacion, usuarioId, monto, TipoOperacionSaldo.COMPRA);
            if (operacion.getEstado() == EstadoOperacionSaldo.APLICADA) {
                return respuesta(usuario, operacion, true);
            }
            validarSaldoSuficiente(usuario, monto);
            usuario.setSaldo(saldoActual(usuario).subtract(normalizar(monto)));
            operacion.setEstado(EstadoOperacionSaldo.APLICADA);
            operacion.setFechaActualizacion(LocalDateTime.now());
            saldoGateway.guardarOperacion(operacion);
            return respuesta(saldoGateway.guardarSaldo(usuario), operacion, false);
        }

        validarSaldoSuficiente(usuario, monto);
        usuario.setSaldo(saldoActual(usuario).subtract(normalizar(monto)));
        Usuario actualizado = saldoGateway.guardarSaldo(usuario);
        OperacionSaldo operacion = nuevaOperacion(
                usuarioId, monto, referencia, TipoOperacionSaldo.COMPRA
        );
        saldoGateway.guardarOperacion(operacion);
        return respuesta(actualizado, operacion, false);
    }

    public ResultadoSaldo reintegrarCompra(String usuarioId, String referencia) {
        validarTexto(usuarioId, "usuarioId");
        validarTexto(referencia, "referencia");
        Usuario usuario = saldoGateway.buscarUsuarioPorIdParaActualizar(usuarioId);
        OperacionSaldo operacion = saldoGateway.buscarOperacionPorReferencia(referencia)
                .orElseThrow(() -> new ConflictoException(
                        "No existe un débito aplicado para la referencia " + referencia
                ));

        if (!usuarioId.equals(operacion.getUsuarioId())
                || operacion.getTipo() != TipoOperacionSaldo.COMPRA) {
            throw new ConflictoException("La referencia no pertenece a esta compra o usuario");
        }
        if (operacion.getEstado() == EstadoOperacionSaldo.REINTEGRADA) {
            return respuesta(usuario, operacion, true);
        }

        usuario.setSaldo(saldoActual(usuario).add(operacion.getMonto()));
        operacion.setEstado(EstadoOperacionSaldo.REINTEGRADA);
        operacion.setFechaActualizacion(LocalDateTime.now());
        saldoGateway.guardarOperacion(operacion);
        return respuesta(saldoGateway.guardarSaldo(usuario), operacion, false);
    }

    private void validarSaldoSuficiente(Usuario usuario, BigDecimal monto) {
        if (saldoActual(usuario).compareTo(normalizar(monto)) < 0) {
            throw new ReglaNegocioException("Saldo insuficiente para efectuar la compra");
        }
    }

    private void validarMismaOperacion(
            OperacionSaldo operacion,
            String usuarioId,
            BigDecimal monto,
            TipoOperacionSaldo tipo
    ) {
        if (!usuarioId.equals(operacion.getUsuarioId())
                || operacion.getTipo() != tipo
                || operacion.getMonto().compareTo(normalizar(monto)) != 0) {
            throw new ConflictoException(
                    "La referencia ya fue usada con otros datos y no puede reutilizarse"
            );
        }
    }

    private void validarEntrada(String usuarioId, BigDecimal monto, String referencia) {
        validarTexto(usuarioId, "usuarioId");
        validarTexto(referencia, "referencia");
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El monto debe ser mayor que cero");
        }
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio");
        }
    }

    private BigDecimal saldoActual(Usuario usuario) {
        return usuario.getSaldo() == null ? BigDecimal.ZERO.setScale(2) : usuario.getSaldo();
    }

    private BigDecimal normalizar(BigDecimal monto) {
        return monto.setScale(2, RoundingMode.HALF_UP);
    }

    private OperacionSaldo nuevaOperacion(
            String usuarioId,
            BigDecimal monto,
            String referencia,
            TipoOperacionSaldo tipo
    ) {
        return new OperacionSaldo(
                null,
                referencia,
                usuarioId,
                normalizar(monto),
                tipo,
                EstadoOperacionSaldo.APLICADA,
                LocalDateTime.now()
        );
    }

    private ResultadoSaldo respuesta(
            Usuario usuario,
            OperacionSaldo operacion,
            boolean idempotente
    ) {
        return new ResultadoSaldo(
                usuario.getIdUsuario(),
                saldoActual(usuario),
                operacion == null ? null : operacion.getReferencia(),
                operacion == null ? null : operacion.getTipo().name(),
                operacion == null ? null : operacion.getEstado().name(),
                idempotente
        );
    }
}
