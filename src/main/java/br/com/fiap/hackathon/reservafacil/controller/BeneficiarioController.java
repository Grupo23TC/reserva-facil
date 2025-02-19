package br.com.fiap.hackathon.reservafacil.controller;

import br.com.fiap.hackathon.reservafacil.mapper.BeneficiarioMapper;
import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.BeneficiarioResponse;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.service.BeneficiarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/beneficiarios")
@RequiredArgsConstructor
public class BeneficiarioController {
    private final BeneficiarioService service;
    private final BeneficiarioMapper mapper;

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody CadastrarBeneficiarioRequest request) {
        service.cadastrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{cns}")
    public ResponseEntity<BeneficiarioResponse> buscarPorCns(@PathVariable String cns) {
        Beneficiario beneficiario = service.buscarPorCns(cns);

        return ResponseEntity.ok(mapper.toResponse(beneficiario));
    }

    @PatchMapping("/{cns}")
    public ResponseEntity<Void> ativar(@PathVariable String cns) {
        service.ativar(cns);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cns}")
    public ResponseEntity<Void> desativar(@PathVariable String cns) {
        service.desativar(cns);

        return ResponseEntity.noContent().build();
    }
}
