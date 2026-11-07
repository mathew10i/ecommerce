package com.ecommerce.notification.infraestructure.sns;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Component
@ConditionalOnProperty(name = "notifications.enabled", havingValue = "true")
public class SnsSmsSender {

    private final SnsClient snsClient;
    private final String countryCode;

    public SnsSmsSender(
            SnsClient snsClient,
            @Value("${notification.phone-country-code:+57}") String countryCode
    ) {
        this.snsClient = snsClient;
        this.countryCode = countryCode;
    }

    public void enviarSms(String mensaje, String numeroTelefono) {
        if (numeroTelefono == null || numeroTelefono.isBlank()) {
            return;
        }

        String destino = numeroTelefono.startsWith("+")
                ? numeroTelefono
                : countryCode + numeroTelefono;

        snsClient.publish(PublishRequest.builder()
                .message(mensaje)
                .phoneNumber(destino)
                .build());
    }
}
