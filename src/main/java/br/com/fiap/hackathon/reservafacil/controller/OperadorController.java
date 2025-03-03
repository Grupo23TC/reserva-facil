package br.com.fiap.hackathon.reservafacil.controller;

import br.com.fiap.hackathon.reservafacil.mapper.BeneficiarioMapper;
import br.com.fiap.hackathon.reservafacil.mapper.OperadorMapper;
import br.com.fiap.hackathon.reservafacil.model.Operador;
import br.com.fiap.hackathon.reservafacil.model.dto.operador.CadastrarOperadorRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.operador.OperadorResponse;
import br.com.fiap.hackathon.reservafacil.service.OperadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/operadores")
@RequiredArgsConstructor
public class OperadorController {
    private final OperadorService service;

    @PostMapping
    public ResponseEntity<OperadorResponse> save(@Valid @RequestBody CadastrarOperadorRequest request) {
        Operador operador = service.cadastrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(OperadorMapper.toResponse(operador));
    }

    @GetMapping("/{cns}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<OperadorResponse> buscarPorCns(@PathVariable String cns) {
        Operador operador = service.buscarPorCns(cns);

        return ResponseEntity.ok(OperadorMapper.toResponse(operador));
    }

    @PatchMapping("/{cns}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<OperadorResponse> ativar(@PathVariable String cns) {
        Operador operador = service.ativar(cns);

        return ResponseEntity.ok(OperadorMapper.toResponse(operador));
    }

    @DeleteMapping("/{cns}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<OperadorResponse> desativar(@PathVariable String cns) {
        Operador operador = service.desativar(cns);

        return ResponseEntity.ok(OperadorMapper.toResponse(operador));
    }
}
