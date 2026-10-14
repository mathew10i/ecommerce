package com.ecommerce.auth.domain.model.gateway;

import com.ecommerce.auth.domain.model.Usuario;

public interface UsuarioGetaway {

    Usuario guardarUsuario(Usuario usuario);
    boolean existeUsuarioPorCorreo(String correo);
    Usuario buscarUsuarioPorCorreo(String correo);

    Usuario buscarUsuarioPorId(String usuarioId);

    Usuario actualizarUsuario(Usuario usuario);

    void eliminarUsuarioPorId(String usuarioId );
}
