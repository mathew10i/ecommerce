package com.ecommerce.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperacionSaldo {
    private String idOperacion;
    private String referencia;
    private String usuarioId;
    private BigDecimal monto;
    private TipoOperacionSaldo tipo;
    private EstadoOperacionSaldo estado;
    private LocalDateTime fechaActualizacion;
}
