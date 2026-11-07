package com.ecommerce.notification.infraestructure.ses;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Component
@ConditionalOnProperty(name = "notifications.enabled", havingValue = "true")
public class SesEmailSender {

    private final SesClient sesClient;
    private final String fromEmail;

    public SesEmailSender(
            SesClient sesClient,
            @Value("${aws.ses.from-email:}") String fromEmail
    ) {
        this.sesClient = sesClient;
        this.fromEmail = fromEmail;
    }

    public void enviarEmail(String destino, String asunto, String mensaje) {
        if (destino == null || destino.isBlank()) {
            return;
        }
        if (fromEmail == null || fromEmail.isBlank()) {
            throw new IllegalStateException("AWS_SES_FROM_EMAIL es obligatorio para enviar correos");
        }

        sesClient.sendEmail(SendEmailRequest.builder()
                .source(fromEmail)
                .destination(Destination.builder().toAddresses(destino).build())
                .message(Message.builder()
                        .subject(Content.builder().data(asunto).charset("UTF-8").build())
                        .body(Body.builder()
                                .text(Content.builder().data(mensaje).charset("UTF-8").build())
                                .build())
                        .build())
                .build());
    }
}
