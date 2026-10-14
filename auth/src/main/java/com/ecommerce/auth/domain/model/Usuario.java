package com.ecommerce.auth.domain.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class Usuario {

    private String idUsuario;
    private String nombre;
    private String correo;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String rol;
    private Integer edad ;
    private String numeroTelefonico;

    private BigDecimal saldo;

}
