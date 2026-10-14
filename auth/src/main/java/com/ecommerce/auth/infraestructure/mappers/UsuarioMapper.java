package com.ecommerce.auth.infraestructure.mappers;

import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.infraestructure.driver_adapter.UsuarioData;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public Usuario toUsuario(UsuarioData usuarioData){
        return new Usuario(
                usuarioData.getIdUsuario(),
                usuarioData.getNombre(),
                usuarioData.getCorreo(),
                usuarioData.getPassword(),
                usuarioData.getRol(),
                usuarioData.getEdad(),
                usuarioData.getNumeroTelefonico(),
                usuarioData.getSaldo()
        );

    }
    public UsuarioData toUsuarioData(Usuario usuario){
        return new UsuarioData(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getPassword(),
                usuario.getRol(),
                usuario.getEdad(),
                usuario.getNumeroTelefonico(),
                usuario.getSaldo(),
                null
        );

    }
}
