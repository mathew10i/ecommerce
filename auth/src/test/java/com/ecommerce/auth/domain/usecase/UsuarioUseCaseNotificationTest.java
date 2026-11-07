package com.ecommerce.auth.domain.usecase;

import com.ecommerce.auth.domain.model.Notificacion;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.gateway.EncrypterGateway;
import com.ecommerce.auth.domain.model.gateway.NotificationGateway;
import com.ecommerce.auth.domain.model.gateway.UsuarioGetaway;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UsuarioUseCaseNotificationTest {

    @Test
    void publicaNotificacionDespuesDeRegistrarUsuario() {
        UsuarioGatewayEnMemoria usuarios = new UsuarioGatewayEnMemoria();
        NotificationGatewayEnMemoria notificaciones = new NotificationGatewayEnMemoria();
        UsuarioUseCase useCase = new UsuarioUseCase(
                usuarios,
                new EncrypterGatewayEnMemoria(),
                notificaciones
        );
        Usuario usuario = new Usuario(
                null,
                "Ana",
                "ana@correo.com",
                "secreto",
                "CLIENTE",
                25,
                "3000000000",
                new BigDecimal("100000")
        );

        useCase.guardarUsuario(usuario);

        assertNotNull(notificaciones.ultima);
        assertEquals("REGISTRO_USUARIO", notificaciones.ultima.tipo());
        assertEquals("ana@correo.com", notificaciones.ultima.email());
        assertEquals("3000000000", notificaciones.ultima.numeroTelefono());
        assertEquals(BigDecimal.ZERO, usuarios.usuario.getSaldo());
    }

    private static class NotificationGatewayEnMemoria implements NotificationGateway {
        private Notificacion ultima;

        @Override
        public void enviarMensaje(Notificacion notificacion) {
            ultima = notificacion;
        }
    }

    private static class EncrypterGatewayEnMemoria implements EncrypterGateway {
        @Override
        public String encrypt(String password) {
            return "hash-" + password;
        }

        @Override
        public Boolean checkPass(String passUser, String passBD) {
            return ("hash-" + passUser).equals(passBD);
        }
    }

    private static class UsuarioGatewayEnMemoria implements UsuarioGetaway {
        private Usuario usuario;

        @Override
        public Usuario guardarUsuario(Usuario usuario) {
            usuario.setIdUsuario("usuario-1");
            this.usuario = usuario;
            return usuario;
        }

        @Override
        public boolean existeUsuarioPorCorreo(String correo) {
            return false;
        }

        @Override
        public Usuario buscarUsuarioPorCorreo(String correo) {
            return usuario;
        }

        @Override
        public Usuario buscarUsuarioPorId(String usuarioId) {
            return usuario;
        }

        @Override
        public Usuario actualizarUsuario(Usuario usuario) {
            this.usuario = usuario;
            return usuario;
        }

        @Override
        public void eliminarUsuarioPorId(String usuarioId) {
            usuario = null;
        }
    }
}
