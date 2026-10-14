package com.ecommerce.auth.infraestructure.entry_points;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.usecase.UsuarioUseCase;
import com.ecommerce.auth.infraestructure.driver_adapter.UsuarioData;
import com.ecommerce.auth.infraestructure.mappers.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.auth.domain.model.RespuestaUsuario;

import java.util.Map;

@RestController
@RequestMapping("/api/ecommerce/usuario")
@RequiredArgsConstructor

public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;
    private final UsuarioMapper usuarioMapper;

    @PostMapping("/save")
    public ResponseEntity<RespuestaUsuario> saveUsuario(
            @RequestBody UsuarioData usuarioData) {


        Usuario usuario =
                usuarioMapper.toUsuario(usuarioData);


        RespuestaUsuario respuesta =
                usuarioUseCase.guardarUsuario(usuario);


        return new ResponseEntity<>(
                respuesta,
                HttpStatus.OK
        );
    }

    @GetMapping("/{idUsuario}")

    public ResponseEntity<?> findUsuarioById(
            @PathVariable("idUsuario") String idUsuario) {

        Usuario usuarioEncontrado =
                usuarioUseCase.buscarUsuarioPorId(idUsuario);

        if (usuarioEncontrado.getIdUsuario() != null) {
            return new ResponseEntity<>(usuarioEncontrado, HttpStatus.OK);
        }

        return new ResponseEntity<>(
                Map.of("mensaje", "Usuario no encontrado."),
                HttpStatus.NOT_FOUND
        );
    }
    @PutMapping("/{usuarioId}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable String usuarioId,
            @RequestBody Usuario usuario) {

        usuario.setIdUsuario(usuarioId);

        Usuario usuarioActualizado =
                usuarioUseCase.actualizarUsuario(usuario);

        return ResponseEntity.ok(usuarioActualizado);
    }
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<String> eliminarUsuario(
            @PathVariable String usuarioId) {

        usuarioUseCase.eliminarUsuarioPorId(usuarioId);

        return ResponseEntity.ok("Usuario eliminado correctamente");
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(
            @RequestBody UsuarioData usuarioData){


        String mensajeRespuesta =
                usuarioUseCase.loginUsuario(
                        usuarioData.getCorreo(),
                        usuarioData.getPassword()
                );


        return ResponseEntity.ok(mensajeRespuesta);
    }
}



