package com.ecommerce.catalogo.infraestructure.driver_adapter;

import com.ecommerce.catalogo.domain.exception.ConflictoException;
import com.ecommerce.catalogo.domain.exception.DependenciaNoDisponibleException;
import com.ecommerce.catalogo.domain.exception.RecursoNoEncontradoException;
import com.ecommerce.catalogo.domain.exception.ReglaNegocioException;
import com.ecommerce.catalogo.domain.model.ResultadoSaldoAuth;
import com.ecommerce.catalogo.domain.model.gateway.AuthGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AuthRestGateway implements AuthGateway {

    private final RestClient authRestClient;

    @Override
    public ResultadoSaldoAuth consultarSaldo(String usuarioId) {
        return ejecutar(() -> authRestClient.get()
                .uri("/api/ecommerce/usuarios/{usuarioId}/saldo", usuarioId)
                .retrieve()
                .body(AuthSaldoResponse.class));
    }

    @Override
    public ResultadoSaldoAuth debitar(String usuarioId, BigDecimal monto, String referencia) {
        return ejecutar(() -> authRestClient.post()
                .uri("/api/ecommerce/usuarios/{usuarioId}/saldo/debitos", usuarioId)
                .body(new MovimientoRequest(monto, referencia))
                .retrieve()
                .body(AuthSaldoResponse.class));
    }

    @Override
    public ResultadoSaldoAuth reintegrar(String usuarioId, String referencia) {
        return ejecutar(() -> authRestClient.post()
                .uri("/api/ecommerce/usuarios/{usuarioId}/saldo/reintegros", usuarioId)
                .body(new ReintegroRequest(referencia))
                .retrieve()
                .body(AuthSaldoResponse.class));
    }

    private ResultadoSaldoAuth ejecutar(PeticionAuth peticion) {
        try {
            AuthSaldoResponse response = peticion.ejecutar();
            if (response == null) {
                throw new DependenciaNoDisponibleException("Auth respondió sin contenido", null);
            }
            return new ResultadoSaldoAuth(
                    response.usuarioId(),
                    response.saldo(),
                    response.referencia(),
                    response.idempotente()
            );
        } catch (RestClientResponseException error) {
            String mensaje = extraerMensaje(error);
            switch (error.getStatusCode().value()) {
                case 404 -> throw new RecursoNoEncontradoException(mensaje);
                case 409 -> throw new ConflictoException(mensaje);
                case 400, 422 -> throw new ReglaNegocioException(mensaje);
                default -> throw new DependenciaNoDisponibleException(
                        "Auth rechazó la operación: " + mensaje,
                        error
                );
            }
        } catch (RestClientException error) {
            throw new DependenciaNoDisponibleException(
                    "No fue posible comunicarse con el microservicio Auth",
                    error
            );
        }
    }

    private String extraerMensaje(RestClientResponseException error) {
        String cuerpo = error.getResponseBodyAsString();
        return cuerpo == null || cuerpo.isBlank() ? error.getMessage() : cuerpo;
    }

    @FunctionalInterface
    private interface PeticionAuth {
        AuthSaldoResponse ejecutar();
    }

    private record MovimientoRequest(BigDecimal monto, String referencia) {
    }

    private record ReintegroRequest(String referencia) {
    }

    private record AuthSaldoResponse(
            String usuarioId,
            BigDecimal saldo,
            String referencia,
            String tipo,
            String estado,
            boolean idempotente
    ) {
    }
}
