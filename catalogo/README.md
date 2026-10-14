# Microservicio Catálogo y carrito

Implementación de la actividad de E-commerce con Java 21, Spring Boot,
PostgreSQL, JPA, APIs REST y Clean Architecture. Este repositorio es
independiente de `auth`; únicamente conserva el identificador del usuario y se
comunica con Auth por HTTP.

## Arquitectura

```text
application/                 servicios transaccionales y configuración de casos de uso
domain/model/                entidades y resultados de negocio
domain/model/gateway/        puertos que necesita el dominio
domain/usecase/              reglas de productos, carrito y compra
infraestructure/driver_adapter/  JPA y cliente REST de Auth
infraestructure/entry_points/    controladores, DTO y manejo de errores
```

Las dependencias apuntan hacia el dominio. Los controladores no calculan
totales ni modifican inventario; esas reglas pertenecen a los casos de uso.

## Ejecución

1. Iniciar las dos bases de datos:

   ```bash
   docker compose up -d
   ```

2. Ejecutar `auth` desde su repositorio en el puerto `8080`:

   ```bash
   mvn spring-boot:run
   ```

3. Ejecutar Catálogo desde este repositorio en el puerto `8081`:

   ```bash
   mvn spring-boot:run
   ```

4. Ejecutar las pruebas unitarias:

   ```bash
   mvn test
   ```

Variables opcionales: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SERVER_PORT` y
`AUTH_BASE_URL`.

## APIs principales

### Productos

| Método | Ruta | Resultado |
|---|---|---|
| `POST` | `/api/catalogo/productos` | Crea un producto |
| `GET` | `/api/catalogo/productos` | Lista productos |
| `GET` | `/api/catalogo/productos/{id}` | Consulta un producto |
| `PUT` | `/api/catalogo/productos/{id}` | Actualiza un producto |
| `DELETE` | `/api/catalogo/productos/{id}` | Elimina un producto |

Ejemplo:

```json
{
  "nombre": "Mouse inalámbrico",
  "descripcion": "Mouse ergonómico Bluetooth",
  "precio": 45000.00,
  "stock": 10,
  "imagenUrl": "https://ejemplo.com/mouse.jpg"
}
```

### Carrito

| Método | Ruta | Resultado |
|---|---|---|
| `POST` | `/api/catalogo/carritos/usuarios/{usuarioId}` | Obtiene o crea el carrito activo |
| `GET` | `/api/catalogo/carritos/{carritoId}` | Consulta contenido y total |
| `POST` | `/api/catalogo/carritos/{carritoId}/items` | Agrega un producto |
| `PATCH` | `/api/catalogo/carritos/{carritoId}/items/{productoId}` | Cambia cantidad |
| `DELETE` | `/api/catalogo/carritos/{carritoId}/items/{productoId}` | Retira producto |
| `POST` | `/api/catalogo/carritos/{carritoId}/comprar` | Ejecuta la compra |

Agregar un producto:

```json
{
  "productoId": "UUID-DEL-PRODUCTO",
  "cantidad": 2
}
```

## Consistencia de la compra

- El carrito y los productos se bloquean durante la confirmación para evitar
  ventas simultáneas por encima del stock.
- Catálogo recalcula precios, subtotales y total en el servidor.
- El débito usa la referencia estable `COMPRA-{carritoId}`. Auth la trata como
  llave de idempotencia y no cobra dos veces.
- Si Auth rechaza el débito, no se modifica inventario ni carrito.
- Si falla la transacción local después del débito, Catálogo solicita un
  reintegro idempotente a Auth.
- Un carrito comprado no puede comprarse ni modificarse otra vez.

Consulta [el MER](docs/MER.md), [la explicación de arquitectura](docs/ARQUITECTURA_SOLID.md)
y [la guía de pruebas](docs/PRUEBAS.md).
