package com.ecommerce.notification.infraestructure.sqs;

import com.ecommerce.notification.domain.EventoNotificacion;
import com.ecommerce.notification.infraestructure.ses.SesEmailSender;
import com.ecommerce.notification.infraestructure.sns.SnsSmsSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Component
@ConditionalOnProperty(name = "notifications.enabled", havingValue = "true")
public class SqsNotificationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqsNotificationListener.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final SnsSmsSender smsSender;
    private final SesEmailSender emailSender;
    private final String queueUrl;

    public SqsNotificationListener(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            SnsSmsSender smsSender,
            SesEmailSender emailSender,
            @Value("${aws.sqs.notification-queue-url:}") String queueUrl
    ) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.smsSender = smsSender;
        this.emailSender = emailSender;
        this.queueUrl = queueUrl;
    }

    @PostConstruct
    void validarConfiguracion() {
        if (queueUrl == null || queueUrl.isBlank()) {
            throw new IllegalStateException(
                    "AWS_SQS_NOTIFICATION_QUEUE_URL es obligatorio cuando NOTIFICATIONS_ENABLED=true"
            );
        }
    }

    @Scheduled(fixedDelayString = "${notification.poll-delay-ms:1000}")
    public void recibirMensajes() {
        var respuesta = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .waitTimeSeconds(10)
                .build());

        for (Message message : respuesta.messages()) {
            procesar(message);
        }
    }

    private void procesar(Message message) {
        try {
            EventoNotificacion evento = objectMapper.readValue(
                    message.body(),
                    EventoNotificacion.class
            );

            emailSender.enviarEmail(evento.email(), evento.tipo(), evento.mensaje());
            smsSender.enviarSms(evento.mensaje(), evento.numeroTelefono());

            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build());
        } catch (Exception exception) {
            LOGGER.error(
                    "No fue posible procesar el mensaje SQS {}. Se reintentara segun la cola.",
                    message.messageId(),
                    exception
            );
        }
    }
}
