# Microservicio Auth

Servicio de usuarios y propietario exclusivo del saldo del e-commerce. La rama
`feat/actividad-3-saldo` incorpora operaciones monetarias controladas e
idempotentes para integrarse con el microservicio Catálogo.

## Requisitos

- Java 21
- PostgreSQL con una base de datos llamada `auth_db`
- Puerto HTTP `8080`

Las credenciales se pueden cambiar con `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`.

## Integracion con notificaciones

Cuando se registra correctamente un usuario, Auth publica en Amazon SQS un
evento `REGISTRO_USUARIO` con correo, telefono y mensaje. La integracion se
activa con `NOTIFICATIONS_ENABLED=true` y requiere
`AWS_SQS_NOTIFICATION_QUEUE_URL`. Las credenciales se obtienen de la cadena
estandar de AWS; no se guardan llaves en el codigo.

Si la publicacion falla, el usuario permanece registrado y el error queda en el
log. Para entrega garantizada en produccion se recomienda agregar el patron
Transactional Outbox.

## Operaciones de saldo

| Método | Ruta | Uso |
|---|---|---|
| `GET` | `/api/ecommerce/usuarios/{usuarioId}/saldo` | Consultar saldo |
| `POST` | `/api/ecommerce/usuarios/{usuarioId}/saldo/recargas` | Recargar saldo |
| `POST` | `/api/ecommerce/usuarios/{usuarioId}/saldo/debitos` | Debitar una compra |
| `POST` | `/api/ecommerce/usuarios/{usuarioId}/saldo/reintegros` | Compensar una compra fallida |

Recarga o débito:

```json
{
  "monto": 250000.00,
  "referencia": "RECARGA-INICIAL-001"
}
```

Reintegro:

```json
{
  "referencia": "COMPRA-<id-del-carrito>"
}
```

La `referencia` funciona como llave de idempotencia: repetir el mismo débito no
descuenta dos veces. El CRUD general ignora cualquier intento de modificar el
saldo; únicamente estas operaciones pueden cambiarlo.
