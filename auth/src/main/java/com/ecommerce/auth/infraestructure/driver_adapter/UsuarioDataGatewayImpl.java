package com.ecommerce.auth.infraestructure.driver_adapter;

import com.ecommerce.auth.domain.exception.RecursoNoEncontradoException;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.gateway.UsuarioGetaway;
import com.ecommerce.auth.infraestructure.mappers.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
@Repository
@RequiredArgsConstructor
public class UsuarioDataGatewayImpl implements UsuarioGetaway {

    private final UsuarioDataJpaRepository usuarioDataJpaRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        UsuarioData usuarioMapeado = usuarioMapper.toUsuarioData(usuario);
        UsuarioData usuarioGuardado = usuarioDataJpaRepository.save(usuarioMapeado);

        return usuarioMapper.toUsuario(usuarioGuardado);

        }
    @Override
    public boolean existeUsuarioPorCorreo(String correo) {
        return usuarioDataJpaRepository.existsByCorreo(correo);
    }

    @Override
    public Usuario buscarUsuarioPorId(String usuarioId) {
        return usuarioDataJpaRepository.findById(usuarioId)
                .map(usuarioData -> usuarioMapper.toUsuario(usuarioData))
                .orElse(new Usuario());
    }

    @Override
    public Usuario actualizarUsuario(Usuario usuario) {

        UsuarioData usuarioData = usuarioDataJpaRepository
                .findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado"
                ));

        if (usuario.getCorreo() != null) {
            usuarioData.setCorreo(usuario.getCorreo());
        }

        if (usuario.getPassword() != null) {
            usuarioData.setPassword(usuario.getPassword());
        }

        if (usuario.getNumeroTelefonico() != null) {
            usuarioData.setNumeroTelefonico(
                    usuario.getNumeroTelefonico()
            );
        }

        if (usuario.getEdad() != null) {
            usuarioData.setEdad(usuario.getEdad());
        }

        UsuarioData usuarioActualizado =
                usuarioDataJpaRepository.save(usuarioData);

        return usuarioMapper.toUsuario(usuarioActualizado);
    }
    @Override
    public void eliminarUsuarioPorId(String usuarioId) {

        if (!usuarioDataJpaRepository.existsById(usuarioId)) {
            throw new RecursoNoEncontradoException(
                    "Usuario no encontrado en la base de datos"
            );
        }

        usuarioDataJpaRepository.deleteById(usuarioId);
    }
    @Override
    public Usuario buscarUsuarioPorCorreo(String correo){

        UsuarioData usuarioData =
                usuarioDataJpaRepository
                        .findByCorreo(correo)
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "Usuario no encontrado"
                        ));


        return usuarioMapper.toUsuario(usuarioData);
    }
}
