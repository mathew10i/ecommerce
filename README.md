# E-commerce con microservicios

Proyecto compuesto por tres servicios independientes:

| Servicio | Puerto | Responsabilidad |
|---|---:|---|
| `auth` | 8080 | Usuarios, autenticacion y saldo |
| `catalogo` | 8081 | Productos, carrito y compras |
| `notification` | sin HTTP | Consume eventos SQS y envia correo/SMS con SES y SNS |

## Flujo de notificacion

1. `auth` registra el usuario en PostgreSQL.
2. `auth` publica un evento JSON en Amazon SQS.
3. `notification` consume el evento.
4. `notification` envia el correo con SES y el SMS con SNS.
5. El mensaje se elimina de SQS solo si ambos envios terminan correctamente.

El evento intercambiado tiene esta forma:

```json
{
  "tipo": "REGISTRO_USUARIO",
  "email": "ana@correo.com",
  "numeroTelefono": "3000000000",
  "mensaje": "Usuario registrado con exito"
}
```

## Ejecucion

Cada carpeta es un proyecto Maven independiente y requiere Java 21.

```bash
cd auth && mvn spring-boot:run
cd catalogo && mvn spring-boot:run
cd notification && mvn spring-boot:run
```

Las notificaciones estan desactivadas por defecto. Para activarlas en `auth` y
`notification`, configure en ambos procesos:

```bash
NOTIFICATIONS_ENABLED=true
AWS_REGION=us-east-1
AWS_SQS_NOTIFICATION_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/123456789012/ecommerce-notifications
```

Configure credenciales mediante la cadena estandar de AWS: variables
`AWS_ACCESS_KEY_ID` y `AWS_SECRET_ACCESS_KEY`, perfil local o rol IAM. El
microservicio `notification` tambien necesita:

```bash
AWS_SES_FROM_EMAIL=remitente-verificado@dominio.com
NOTIFICATION_PHONE_COUNTRY_CODE=+57
```

`AWS_ENDPOINT` es opcional y permite usar un emulador compatible, como
LocalStack. En AWS, SES debe tener verificado el remitente y SNS debe tener
permisos para publicar SMS. Conviene configurar una cola de mensajes muertos
(DLQ) en SQS para los eventos que superen el numero de reintentos.

Consulte las instrucciones especificas en [`auth/README.md`](auth/README.md),
[`catalogo/README.md`](catalogo/README.md) y
[`notification/README.md`](notification/README.md).
