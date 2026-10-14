package com.ecommerce.catalogo.application;

import com.ecommerce.catalogo.domain.exception.DependenciaNoDisponibleException;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.CompraPreparada;
import com.ecommerce.catalogo.domain.model.ResultadoCompra;
import com.ecommerce.catalogo.domain.model.ResultadoSaldoAuth;
import com.ecommerce.catalogo.domain.model.gateway.AuthGateway;
import com.ecommerce.catalogo.domain.usecase.CompraUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class CompraApplicationService {

    private final CompraUseCase compraUseCase;
    private final AuthGateway authGateway;
    private final TransactionTemplate transactionTemplate;

    public ResultadoCompra comprar(String carritoId) {
        AtomicBoolean debitoAplicado = new AtomicBoolean(false);
        AtomicReference<CompraPreparada> preparadaRef = new AtomicReference<>();

        try {
            ResultadoCompra resultado = transactionTemplate.execute(status -> {
                CompraPreparada preparada = compraUseCase.preparar(carritoId);
                preparadaRef.set(preparada);

                ResultadoSaldoAuth saldo = authGateway.debitar(
                        preparada.carrito().getUsuarioId(),
                        preparada.total(),
                        preparada.referencia()
                );
                debitoAplicado.set(true);
                Carrito confirmado = compraUseCase.confirmar(preparada);

                return new ResultadoCompra(
                        confirmado.getId(),
                        confirmado.getUsuarioId(),
                        confirmado.getEstado().name(),
                        confirmado.getTotal(),
                        saldo.saldo(),
                        preparada.referencia()
                );
            });

            if (resultado == null) {
                throw new IllegalStateException("La transacción de compra no produjo resultado");
            }
            return resultado;
        } catch (RuntimeException errorCompra) {
            compensarSiCorresponde(debitoAplicado.get(), preparadaRef.get());
            throw errorCompra;
        }
    }

    private void compensarSiCorresponde(
            boolean debitoAplicado,
            CompraPreparada preparada
    ) {
        if (!debitoAplicado || preparada == null) {
            return;
        }
        try {
            authGateway.reintegrar(
                    preparada.carrito().getUsuarioId(),
                    preparada.referencia()
            );
        } catch (RuntimeException errorCompensacion) {
            throw new DependenciaNoDisponibleException(
                    "La compra falló y no fue posible confirmar el reintegro automático. "
                            + "Referencia: " + preparada.referencia(),
                    errorCompensacion
            );
        }
    }
}
