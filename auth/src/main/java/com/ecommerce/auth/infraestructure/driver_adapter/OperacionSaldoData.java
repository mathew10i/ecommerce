package com.ecommerce.auth.infraestructure.driver_adapter;

import com.ecommerce.auth.domain.model.EstadoOperacionSaldo;
import com.ecommerce.auth.domain.model.TipoOperacionSaldo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operaciones_saldo")
public class OperacionSaldoData {

    @Id
    @UuidGenerator
    private String idOperacion;

    @Column(nullable = false, unique = true, length = 100)
    private String referencia;

    @Column(nullable = false)
    private String usuarioId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoOperacionSaldo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOperacionSaldo estado;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
}
