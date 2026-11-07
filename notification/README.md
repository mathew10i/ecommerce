# Microservicio Notification

Consumidor asincrono de los eventos que Auth publica en Amazon SQS. Por cada
evento envia correo con Amazon SES y SMS con Amazon SNS.

## Requisitos

- Java 21 y Maven.
- Una cola SQS, preferiblemente con DLQ.
- Un remitente verificado en SES.
- Permisos IAM para `sqs:ReceiveMessage`, `sqs:DeleteMessage`,
  `sns:Publish` y `ses:SendEmail`.

## Configuracion

```bash
export NOTIFICATIONS_ENABLED=true
export AWS_REGION=us-east-1
export AWS_SQS_NOTIFICATION_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/123456789012/ecommerce-notifications
export AWS_SES_FROM_EMAIL=remitente-verificado@dominio.com
export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
mvn spring-boot:run
```

`NOTIFICATION_PHONE_COUNTRY_CODE` usa `+57` por defecto. Si el numero del evento
ya comienza con `+`, se conserva. Para LocalStack se puede definir
`AWS_ENDPOINT=http://localhost:4566`.

El servicio usa long polling de SQS. Un mensaje se elimina solo despues de
enviar correo y SMS; si ocurre un error, permanece visible para que SQS lo
reintente.
