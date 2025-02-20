package br.com.fiap.hackathon.reservafacil.controller;

import br.com.fiap.hackathon.reservafacil.model.dto.usuario.AtualizarSenhaRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.usuario.CadastrarUsuarioRequest;
import br.com.fiap.hackathon.reservafacil.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<Void> cadastrar(@Valid @RequestBody CadastrarUsuarioRequest request) {
        service.salvarUsuario(request.cns(), request.senha(), request.role());

        return ResponseEntity.status(HttpStatus.CREATED).build();
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
    public ResponseEntity<Void> ativar(@PathVariable String cns) {
        service.ativar(cns);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cns}")
    public ResponseEntity<Void> intativar(@PathVariable String cns) {
        service.desativar(cns);

        return ResponseEntity.noContent().build();
    }
}
