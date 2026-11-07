package com.ecommerce.auth.domain.model;

public record Notificacion(
        String tipo,
        String email,
        String numeroTelefono,
        String mensaje
) {
}
