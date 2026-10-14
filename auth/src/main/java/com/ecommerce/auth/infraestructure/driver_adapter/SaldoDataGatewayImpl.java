package com.ecommerce.auth.infraestructure.driver_adapter;

import com.ecommerce.auth.domain.exception.RecursoNoEncontradoException;
import com.ecommerce.auth.domain.model.OperacionSaldo;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.gateway.SaldoGateway;
import com.ecommerce.auth.infraestructure.mappers.OperacionSaldoMapper;
import com.ecommerce.auth.infraestructure.mappers.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SaldoDataGatewayImpl implements SaldoGateway {

    private final UsuarioDataJpaRepository usuarioRepository;
    private final OperacionSaldoJpaRepository operacionRepository;
    private final UsuarioMapper usuarioMapper;
    private final OperacionSaldoMapper operacionMapper;

    @Override
    public Usuario buscarUsuarioPorId(String usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(usuarioMapper::toUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    @Override
    public Usuario buscarUsuarioPorIdParaActualizar(String usuarioId) {
        return usuarioRepository.findByIdForUpdate(usuarioId)
                .map(usuarioMapper::toUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    @Override
    public Usuario guardarSaldo(Usuario usuario) {
        UsuarioData data = usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        data.setSaldo(usuario.getSaldo());
        return usuarioMapper.toUsuario(usuarioRepository.save(data));
    }

    @Override
    public Optional<OperacionSaldo> buscarOperacionPorReferencia(String referencia) {
        return operacionRepository.findByReferencia(referencia).map(operacionMapper::toDomain);
    }

    @Override
    public OperacionSaldo guardarOperacion(OperacionSaldo operacion) {
        OperacionSaldoData guardada = operacionRepository.save(operacionMapper.toData(operacion));
        return operacionMapper.toDomain(guardada);
    }
}
