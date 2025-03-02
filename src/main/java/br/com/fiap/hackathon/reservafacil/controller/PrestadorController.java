package br.com.fiap.hackathon.reservafacil.controller;

import br.com.fiap.hackathon.reservafacil.model.dto.prestador.AtualizarPrestadorRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.CadastrarPrestadorRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.PrestadorResponseDTO;
import br.com.fiap.hackathon.reservafacil.service.impl.PrestadorServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/prestadores")
@RequiredArgsConstructor
public class PrestadorController {
    private final PrestadorServiceImpl service;

    @GetMapping
    public ResponseEntity<Page<PrestadorResponseDTO>> listarPrestadores(
            @PageableDefault(page = 0, size = 20) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(service.listarPrestadores(pageable));
    }

    @GetMapping("/cidade")
    public ResponseEntity<List<PrestadorResponseDTO>> buscarPrestadoresPorCidade(@RequestParam String cidade) {
        List<PrestadorResponseDTO> listaPrestadoresPorCidade = service.buscarPrestadorPorCidade(cidade);
        return ResponseEntity.status(HttpStatus.OK).body(listaPrestadoresPorCidade);
    }

    @GetMapping("/medicamentos")
    public ResponseEntity<List<PrestadorResponseDTO>> buscarPrestadoresPorMedicamento(@RequestParam String nomeMedicamento) {
        List<PrestadorResponseDTO> listaPrestadores = service.buscarPrestadoresPorMedicamentoDisponivel(nomeMedicamento);
        return ResponseEntity.status(HttpStatus.OK).body(listaPrestadores);
    }

    @GetMapping("/cidade-medicamentos")
    public ResponseEntity<List<PrestadorResponseDTO>> buscarPrestadoresPorMedicamentoECidade(@RequestParam String cidade,
                                                                                                 @RequestParam String nomeMedicamento) {
        List<PrestadorResponseDTO> listaPrestadores = service.buscarPrestadoresPorMedicamentoECidade(cidade, nomeMedicamento);
        return ResponseEntity.status(HttpStatus.OK).body(listaPrestadores);
    }

    @PostMapping
    public ResponseEntity<PrestadorResponseDTO> cadastrarPrestador(@RequestBody CadastrarPrestadorRequestDTO prestadorRequestDTO) {
        PrestadorResponseDTO prestadorSalvo = service.cadastrarPrestador(prestadorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(prestadorSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrestadorResponseDTO> atualizarPrestador(
            @Valid @RequestBody AtualizarPrestadorRequestDTO prestador,
            @PathVariable UUID id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(service.atualizarPrestador(id, prestador));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.excluirPrestador(id);
        return ResponseEntity.noContent().build();
    }
}
