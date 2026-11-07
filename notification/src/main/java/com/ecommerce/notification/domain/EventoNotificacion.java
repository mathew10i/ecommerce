package com.ecommerce.notification.domain;

public record EventoNotificacion(
        String tipo,
        String email,
        String numeroTelefono,
        String mensaje
) {
}
