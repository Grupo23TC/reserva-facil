package br.com.fiap.hackathon.reservafacil.controller;

import br.com.fiap.hackathon.reservafacil.mapper.BeneficiarioMapper;
import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.BeneficiarioResponse;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.service.impl.BeneficiarioServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/beneficiarios")
@RequiredArgsConstructor
public class BeneficiarioController {
    private final BeneficiarioServiceImpl service;

    @PostMapping
    public ResponseEntity<BeneficiarioResponse> save(@Valid @RequestBody CadastrarBeneficiarioRequest request) {
        Beneficiario beneficiario = service.cadastrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(BeneficiarioMapper.toResponse(beneficiario));
    }

    @GetMapping("/{cns}")
    public ResponseEntity<BeneficiarioResponse> buscarPorCns(@PathVariable String cns) {
        Beneficiario beneficiario = service.buscarPorCns(cns);

        return ResponseEntity.ok(BeneficiarioMapper.toResponse(beneficiario));
    }

    @PatchMapping("/{cns}")
    public ResponseEntity<BeneficiarioResponse> ativar(@PathVariable String cns) {
        Beneficiario beneficiario = service.ativar(cns);

        return ResponseEntity.ok(BeneficiarioMapper.toResponse(beneficiario));
    }

    @DeleteMapping("/{cns}")
    public ResponseEntity<BeneficiarioResponse> desativar(@PathVariable String cns) {
        Beneficiario beneficiario = service.desativar(cns);

        return ResponseEntity.ok(BeneficiarioMapper.toResponse(beneficiario));
    }
}
