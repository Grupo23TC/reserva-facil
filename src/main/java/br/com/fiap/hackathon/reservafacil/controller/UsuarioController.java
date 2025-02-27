package br.com.fiap.hackathon.reservafacil.controller;

import br.com.fiap.hackathon.reservafacil.mapper.UsuarioMapper;
import br.com.fiap.hackathon.reservafacil.model.dto.usuario.AtualizarSenhaRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.usuario.CadastrarUsuarioRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.usuario.UsuarioResponse;
import br.com.fiap.hackathon.reservafacil.service.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioServiceImpl service;

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastrarUsuarioRequest request) {
        var usuario = service.salvarUsuario(request.cns(), request.senha(), request.role());

        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toUsuarioResponse(usuario));
    }

    @PatchMapping("/{cns}")
    public ResponseEntity<Void> atualizarSenha(
            @Valid @RequestBody AtualizarSenhaRequest request,
            @PathVariable String cns
    ) {
        service.atualizarSenhaUsuario(cns, request.novaSenha());

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{cns}")
    public ResponseEntity<UsuarioResponse> ativar(@PathVariable String cns) {
        var usuario = service.ativar(cns);

        return ResponseEntity.ok(UsuarioMapper.toUsuarioResponse(usuario));
    }

    @DeleteMapping("/{cns}")
    public ResponseEntity<UsuarioResponse> intativar(@PathVariable String cns) {
        var usuario = service.desativar(cns);

        return ResponseEntity.ok(UsuarioMapper.toUsuarioResponse(usuario));
    }
}
