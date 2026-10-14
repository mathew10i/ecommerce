package com.ecommerce.auth.domain.model;


public class RespuestaUsuario {

    private String mensaje;
    private Usuario usuario;


    public RespuestaUsuario(String mensaje, Usuario usuario) {
        this.mensaje = mensaje;
        this.usuario = usuario;
    }


    public String getMensaje() {
        return mensaje;
    }


    public Usuario getUsuario() {
        return usuario;
    }
}