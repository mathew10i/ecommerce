package com.ecommerce.auth.infraestructure.entry_points;

import com.ecommerce.auth.application.SaldoApplicationService;
import com.ecommerce.auth.domain.model.ResultadoSaldo;
import com.ecommerce.auth.infraestructure.entry_points.dto.MovimientoSaldoRequest;
import com.ecommerce.auth.infraestructure.entry_points.dto.ReintegroSaldoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ecommerce/usuarios")
@RequiredArgsConstructor
public class SaldoController {

    private final SaldoApplicationService saldoService;

    @GetMapping("/{usuarioId}/saldo")
    public ResponseEntity<ResultadoSaldo> consultar(@PathVariable String usuarioId) {
        return ResponseEntity.ok(saldoService.consultar(usuarioId));
    }

    @PostMapping("/{usuarioId}/saldo/recargas")
    public ResponseEntity<ResultadoSaldo> recargar(
            @PathVariable String usuarioId,
            @RequestBody MovimientoSaldoRequest request
    ) {
        return ResponseEntity.ok(
                saldoService.recargar(usuarioId, request.monto(), request.referencia())
        );
    }

    @PostMapping("/{usuarioId}/saldo/debitos")
    public ResponseEntity<ResultadoSaldo> debitar(
            @PathVariable String usuarioId,
            @RequestBody MovimientoSaldoRequest request
    ) {
        return ResponseEntity.ok(
                saldoService.debitarCompra(usuarioId, request.monto(), request.referencia())
        );
    }

    @PostMapping("/{usuarioId}/saldo/reintegros")
    public ResponseEntity<ResultadoSaldo> reintegrar(
            @PathVariable String usuarioId,
            @RequestBody ReintegroSaldoRequest request
    ) {
        return ResponseEntity.ok(
                saldoService.reintegrarCompra(usuarioId, request.referencia())
        );
    }
}
