package com.ecommerce.catalogo.infraestructure.driver_adapter;

import com.ecommerce.catalogo.domain.model.EstadoCarrito;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "carritos",
        indexes = @Index(name = "idx_carrito_usuario_estado", columnList = "usuario_id,estado"),
        uniqueConstraints = @UniqueConstraint(
                name = "uk_carrito_activo_usuario",
                columnNames = {"usuario_id", "carrito_activo"}
        )
)
public class CarritoData {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCarrito estado;

    // TRUE mientras está activo y NULL cuando se compra. PostgreSQL permite varios
    // NULL en una restricción UNIQUE, pero solo un TRUE por usuario.
    @Column(name = "carrito_activo")
    private Boolean carritoActivo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaCompra;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarritoData> items = new ArrayList<>();

    @Version
    private Long version;
}
