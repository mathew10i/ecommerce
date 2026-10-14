package com.ecommerce.catalogo.domain.exception;

public class DependenciaNoDisponibleException extends RuntimeException {
    public DependenciaNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
