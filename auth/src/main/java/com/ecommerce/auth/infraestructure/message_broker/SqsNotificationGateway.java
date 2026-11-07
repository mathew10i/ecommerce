package com.ecommerce.auth.infraestructure.message_broker;

import com.ecommerce.auth.domain.model.Notificacion;
import com.ecommerce.auth.domain.model.gateway.NotificationGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notifications.enabled", havingValue = "true")
public class SqsNotificationGateway implements NotificationGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqsNotificationGateway.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.notification-queue-url:}")
    private String queueUrl;

    @PostConstruct
    void validarConfiguracion() {
        if (queueUrl == null || queueUrl.isBlank()) {
            throw new IllegalStateException(
                    "AWS_SQS_NOTIFICATION_QUEUE_URL es obligatorio cuando NOTIFICATIONS_ENABLED=true"
            );
        }
    }

    @Override
    public void enviarMensaje(Notificacion notificacion) {
        try {
            String cuerpo = objectMapper.writeValueAsString(notificacion);
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(cuerpo)
                    .build());
        } catch (JsonProcessingException | SdkException exception) {
            LOGGER.error("No fue posible publicar la notificacion de registro", exception);
        }
    }
}
