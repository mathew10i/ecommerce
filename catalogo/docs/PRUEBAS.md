# Guía de pruebas y evidencias

Use Postman o Insomnia y conserve una captura de la petición y la respuesta de
cada escenario.

## Preparación

1. Crear un usuario en Auth mediante `POST /api/ecommerce/usuario/save`.
2. Guardar su `idUsuario`.
3. Recargar `$250.000` con una referencia única:

   `POST /api/ecommerce/usuarios/{usuarioId}/saldo/recargas`

   ```json
   {"monto": 250000, "referencia": "RECARGA-DEMO-001"}
   ```

4. Crear un mouse de `$45.000`, stock `10`, y unos audífonos de `$80.000`,
   stock `5`.
5. Crear el carrito con
   `POST /api/catalogo/carritos/usuarios/{usuarioId}`.

## Compra exitosa

1. Agregar dos mouses y unos audífonos.
2. Consultar el carrito: debe mostrar subtotales `$90.000` y `$80.000`, total
   `$170.000`.
3. Ejecutar `POST /api/catalogo/carritos/{carritoId}/comprar`.
4. Verificar saldo restante `$80.000`, stock de mouse `8`, stock de audífonos
   `4` y estado `COMPRADO`.
5. Repetir la misma compra: debe responder `409 Conflict` y conservar el saldo.

## Saldo insuficiente

1. Crear otro carrito activo y productos cuyo total supere el saldo disponible.
2. Ejecutar la compra.
3. Debe responder `422 Unprocessable Entity`.
4. Verificar que ni el saldo ni el stock cambiaron.

## Stock insuficiente

1. Crear un producto con stock `1`.
2. Intentar agregar o comprar cantidad `2`.
3. Debe responder `422 Unprocessable Entity`.
4. Verificar que el saldo no cambió y el inventario continúa en `1`.

## Validaciones adicionales

- Precio `0` o negativo: `422`.
- Stock negativo: `422`.
- Cantidad `0` o negativa: `422`.
- Producto, carrito o usuario inexistente: `404`.
- Auth apagado: `503 Service Unavailable`.
- Agregar dos veces el mismo producto: un solo ítem con cantidad acumulada.
