package com.ecommerce.catalogo.infraestructure.entry_points;

import com.ecommerce.catalogo.application.ProductoApplicationService;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.infraestructure.entry_points.dto.ProductoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoApplicationService service;

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request.toDomain()));
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{productoId}")
    public ResponseEntity<Producto> consultar(@PathVariable String productoId) {
        return ResponseEntity.ok(service.consultar(productoId));
    }

    @PutMapping("/{productoId}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable String productoId,
            @RequestBody ProductoRequest request
    ) {
        return ResponseEntity.ok(service.actualizar(productoId, request.toDomain()));
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<Void> eliminar(@PathVariable String productoId) {
        service.eliminar(productoId);
        return ResponseEntity.noContent().build();
    }
}
