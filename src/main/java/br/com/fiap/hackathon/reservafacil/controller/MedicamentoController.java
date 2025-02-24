package br.com.fiap.hackathon.reservafacil.controller;

import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.AtualizarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.CadastrarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.MedicamentoResponseDTO;
import br.com.fiap.hackathon.reservafacil.service.MedicamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/medicamentos")
@RequiredArgsConstructor
public class MedicamentoController {
    private final MedicamentoService service;

    @PostMapping
    public ResponseEntity<MedicamentoResponseDTO> cadastrar(@Valid @RequestBody CadastrarMedicamentoRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrarMedicamento(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> buscar(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.buscarMedicamento(id));
    }

    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDTO>> listar() {
        return ResponseEntity.status(HttpStatus.OK).body(service.listarMedicamentos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> atualizar(
            @Valid @RequestBody AtualizarMedicamentoRequestDTO medicamento,
            @PathVariable UUID id
    ) {


        return ResponseEntity.status(HttpStatus.OK).body(service.atualizarMedicamento(id, medicamento));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        service.excluirMedicamento(id);

        return ResponseEntity.noContent().build();
    }
}
