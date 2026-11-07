package com.ecommerce.auth.infraestructure.message_broker;

import com.ecommerce.auth.domain.model.Notificacion;
import com.ecommerce.auth.domain.model.gateway.NotificationGateway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "notifications.enabled",
        havingValue = "false",
        matchIfMissing = true
)
public class NoOpNotificationGateway implements NotificationGateway {

    @Override
    public void enviarMensaje(Notificacion notificacion) {
        // Permite ejecutar Auth sin AWS durante desarrollo y pruebas locales.
    }
}
