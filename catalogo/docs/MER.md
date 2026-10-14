# Modelo Entidad-Relación

```mermaid
erDiagram
    USUARIO ||--o{ OPERACION_SALDO : registra
    USUARIO ||--o{ CARRITO : identifica_por_API
    CARRITO ||--o{ ITEM_CARRITO : contiene
    PRODUCTO ||--o{ ITEM_CARRITO : referencia

    USUARIO {
        string id_usuario PK
        string nombre
        string correo UK
        decimal saldo
        long version
    }
    OPERACION_SALDO {
        string id_operacion PK
        string referencia UK
        string usuario_id
        decimal monto
        string tipo
        string estado
        datetime fecha_actualizacion
    }
    PRODUCTO {
        string id PK
        string nombre
        string descripcion
        decimal precio
        int stock
        string imagen_url
        long version
    }
    CARRITO {
        string id PK
        string usuario_id
        string estado
        boolean carrito_activo
        decimal total
        datetime fecha_creacion
        datetime fecha_compra
        long version
    }
    ITEM_CARRITO {
        string id PK
        string carrito_id FK
        string producto_id FK
        int cantidad
        decimal precio_unitario
        decimal subtotal
    }
```

## Interpretación

- `Usuario` y `OperacionSaldo` pertenecen exclusivamente a la base de Auth.
- `Producto`, `Carrito` e `ItemCarrito` pertenecen exclusivamente a la base de
  Catálogo.
- La línea `Usuario–Carrito` es una relación lógica mediante `usuario_id` y una
  API REST; **no es una clave foránea física** entre bases de datos.
- Un carrito contiene muchos ítems y cada ítem referencia un producto.
- La combinación `carrito_id + producto_id` es única, por lo que agregar el
  mismo producto incrementa la cantidad en lugar de duplicar filas.
- `precio_unitario` y `subtotal` conservan la fotografía monetaria usada por el
  carrito; al comprar se recalculan con el precio vigente.
- La combinación `usuario_id + carrito_activo` impide más de un carrito activo
  por usuario. Al comprar, `carrito_activo` pasa a `NULL`.
