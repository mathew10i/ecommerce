package com.ecommerce.auth.domain.usecase;
import com.ecommerce.auth.domain.model.gateway.EncrypterGateway;

import java.math.BigDecimal;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.gateway.UsuarioGetaway;
import lombok.RequiredArgsConstructor;
import com.ecommerce.auth.domain.model.RespuestaUsuario;


@RequiredArgsConstructor
public class UsuarioUseCase {


    private final UsuarioGetaway usuarioGetaway;
    private final EncrypterGateway encrypterGateway;



    public RespuestaUsuario guardarUsuario(Usuario usuario) {


        validarNoNulo(usuario.getNombre(), "nombre");
        validarNoNulo(usuario.getCorreo(), "correo");
        validarNoNulo(usuario.getPassword(), "password");
        validarNoNulo(usuario.getRol(), "rol");
        validarNoNulo(usuario.getEdad(), "edad");
        validarNoNulo(usuario.getNumeroTelefonico(), "numeroTelefonico");

        // El alta general de usuarios nunca asigna dinero. Las recargas se realizan
        // exclusivamente a través del caso de uso de saldo.
        usuario.setSaldo(BigDecimal.ZERO);


        // Regla de negocio: no permite menores de 18 años
        if (usuario.getEdad() < 18) {

            return new RespuestaUsuario(
                    "Usuario menor de edad, no se puede guardar",
                    null
            );
        }


        // Validar correo duplicado
        if (usuarioGetaway.existeUsuarioPorCorreo(
                usuario.getCorreo()
        )) {

            throw new IllegalStateException(
                    "Ya existe un usuario registrado con este correo."
            );
        }

        String passwordEncrypt =
                encrypterGateway.encrypt(
                        usuario.getPassword()
                );

        usuario.setPassword(passwordEncrypt);

        Usuario usuarioGuardado =
                usuarioGetaway.guardarUsuario(usuario);

        return new RespuestaUsuario(
                "Usuario creado correctamente",
                usuarioGuardado
        );
    }





    public Usuario buscarUsuarioPorId(String usuarioId) {

        return usuarioGetaway.buscarUsuarioPorId(usuarioId);
    }





    public Usuario actualizarUsuario(Usuario usuario) {


        // Validar que el usuario exista
        Usuario usuarioActual =
                usuarioGetaway.buscarUsuarioPorId(
                        usuario.getIdUsuario()
                );



        // EDAD
        // Permite usuarios de 18 años o más
        if (usuario.getEdad() != null) {

            if (usuario.getEdad() < 18) {

                throw new IllegalArgumentException(
                        "Usuario menor de edad, no se puede actualizar"
                );
            }
        }




        // NOMBRE NO SE PUEDE MODIFICAR
        if (usuario.getNombre() != null) {

            throw new IllegalArgumentException(
                    "El nombre no se puede modificar"
            );
        }




        // ROL NO SE PUEDE MODIFICAR
        if (usuario.getRol() != null) {

            throw new IllegalArgumentException(
                    "El rol no se puede modificar"
            );
        }





        // CORREO
        if (usuario.getCorreo() != null) {


            if (usuario.getCorreo().isBlank()) {

                throw new IllegalArgumentException(
                        "El correo no puede estar vacío"
                );
            }



            if (!usuario.getCorreo()
                    .equals(usuarioActual.getCorreo())
                    &&
                    usuarioGetaway.existeUsuarioPorCorreo(
                            usuario.getCorreo())) {


                throw new IllegalStateException(
                        "Ya existe un usuario registrado con este correo"
                );
            }
        }





        // PASSWORD
        if (usuario.getPassword() != null) {

            if (usuario.getPassword().isBlank()) {

                throw new IllegalArgumentException(
                        "La contraseña no puede estar vacía"
                );
            }

            String passwordEncrypt =
                    encrypterGateway.encrypt(
                            usuario.getPassword()
                    );

            usuario.setPassword(passwordEncrypt);
        }





        // TELÉFONO
        if (usuario.getNumeroTelefonico() != null) {


            if (usuario.getNumeroTelefonico().isBlank()) {

                throw new IllegalArgumentException(
                        "El número telefónico no puede estar vacío"
                );
            }
        }



        return usuarioGetaway.actualizarUsuario(usuario);
    }





    public void eliminarUsuarioPorId(String usuarioId) {

        usuarioGetaway.eliminarUsuarioPorId(usuarioId);
    }





    public String loginUsuario(
            String correo,
            String password
    ){


        // Validar correo vacío o nulo
        if(correo == null || correo.isBlank()){

            throw new IllegalArgumentException(
                    "El campo correo no puede estar vacío"
            );
        }



        // Validar contraseña vacía o nula
        if(password == null || password.isBlank()){

            throw new IllegalArgumentException(
                    "El campo password no puede estar vacío"
            );
        }



        // Buscar usuario en base de datos
        Usuario usuario =
                usuarioGetaway.buscarUsuarioPorCorreo(correo);



        // Validar contraseña
        if (!encrypterGateway.checkPass(
                password,
                usuario.getPassword()
        )) {

            throw new IllegalArgumentException(
                    "Contraseña incorrecta"
            );
        }



        return "Login exitoso";
    }





    private void validarNoNulo(
            Object valor,
            String campo
    ) {


        if (valor == null) {

            throw new IllegalArgumentException(
                    "El campo " + campo + " no puede estar vacío"
            );
        }


        if (valor instanceof String && ((String) valor).isBlank()) {

            throw new IllegalArgumentException(
                    "El campo " + campo + " no puede estar vacío"
            );
        }
    }
}
