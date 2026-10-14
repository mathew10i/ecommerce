package com.ecommerce.catalogo.infraestructure.driver_adapter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "items_carrito",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_item_carrito_producto",
                columnNames = {"carrito_id", "producto_id"}
        )
)
public class ItemCarritoData {

    @Id
    @UuidGenerator
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrito_id", nullable = false)
    private CarritoData carrito;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoData producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;
}
