package com.ecommerce.auth.infraestructure.entry_points;

import com.ecommerce.auth.domain.exception.ConflictoException;
import com.ecommerce.auth.domain.exception.RecursoNoEncontradoException;
import com.ecommerce.auth.domain.exception.ReglaNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException error) {
        return respuesta(HttpStatus.NOT_FOUND, error.getMessage());
    }

    @ExceptionHandler({ReglaNegocioException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> manejarValidacion(RuntimeException error) {
        return respuesta(HttpStatus.UNPROCESSABLE_ENTITY, error.getMessage());
    }

    @ExceptionHandler({ConflictoException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> manejarConflicto(RuntimeException error) {
        return respuesta(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarGeneral(Exception error) {
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
