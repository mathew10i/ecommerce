package com.ecommerce.catalogo.infraestructure.entry_points;

import com.ecommerce.catalogo.domain.exception.ConflictoException;
import com.ecommerce.catalogo.domain.exception.DependenciaNoDisponibleException;
import com.ecommerce.catalogo.domain.exception.RecursoNoEncontradoException;
import com.ecommerce.catalogo.domain.exception.ReglaNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException error) {
        return respuesta(HttpStatus.NOT_FOUND, error.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> reglaNegocio(ReglaNegocioException error) {
        return respuesta(HttpStatus.UNPROCESSABLE_ENTITY, error.getMessage());
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorResponse> conflicto(ConflictoException error) {
        return respuesta(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(DependenciaNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> dependencia(DependenciaNoDisponibleException error) {
        return respuesta(HttpStatus.SERVICE_UNAVAILABLE, error.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> general(Exception error) {
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno");
    }

    private ResponseEntity<ErrorResponse> respuesta(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(
                new ErrorResponse(estado.value(), estado.getReasonPhrase(), mensaje, LocalDateTime.now())
        );
    }

    public record ErrorResponse(
            int status,
            String error,
            String mensaje,
            LocalDateTime timestamp
    ) {
    }
}
