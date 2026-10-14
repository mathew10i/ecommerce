package com.ecommerce.catalogo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    private String id;
    private String usuarioId;
    private EstadoCarrito estado;
    private BigDecimal total;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaCompra;
    private List<ItemCarrito> items = new ArrayList<>();
}
