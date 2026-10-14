package com.ecommerce.catalogo.infraestructure.entry_points;

import com.ecommerce.catalogo.application.CarritoApplicationService;
import com.ecommerce.catalogo.application.CompraApplicationService;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.ResultadoCompra;
import com.ecommerce.catalogo.infraestructure.entry_points.dto.AgregarItemRequest;
import com.ecommerce.catalogo.infraestructure.entry_points.dto.CambiarCantidadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogo/carritos")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoApplicationService carritoService;
    private final CompraApplicationService compraService;

    @PostMapping("/usuarios/{usuarioId}")
    public ResponseEntity<Carrito> obtenerOCrear(@PathVariable String usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerOCrearActivo(usuarioId));
    }

    @GetMapping("/{carritoId}")
    public ResponseEntity<Carrito> consultar(@PathVariable String carritoId) {
        return ResponseEntity.ok(carritoService.consultar(carritoId));
    }

    @PostMapping("/{carritoId}/items")
    public ResponseEntity<Carrito> agregar(
            @PathVariable String carritoId,
            @RequestBody AgregarItemRequest request
    ) {
        return ResponseEntity.ok(
                carritoService.agregarProducto(carritoId, request.productoId(), request.cantidad())
        );
    }

    @PatchMapping("/{carritoId}/items/{productoId}")
    public ResponseEntity<Carrito> cambiarCantidad(
            @PathVariable String carritoId,
            @PathVariable String productoId,
            @RequestBody CambiarCantidadRequest request
    ) {
        return ResponseEntity.ok(
                carritoService.cambiarCantidad(carritoId, productoId, request.cantidad())
        );
    }

    @DeleteMapping("/{carritoId}/items/{productoId}")
    public ResponseEntity<Carrito> retirar(
            @PathVariable String carritoId,
            @PathVariable String productoId
    ) {
        return ResponseEntity.ok(carritoService.retirarProducto(carritoId, productoId));
    }

    @PostMapping("/{carritoId}/comprar")
    public ResponseEntity<ResultadoCompra> comprar(@PathVariable String carritoId) {
        return ResponseEntity.ok(compraService.comprar(carritoId));
    }
}
