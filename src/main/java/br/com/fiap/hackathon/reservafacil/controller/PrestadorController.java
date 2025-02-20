package br.com.fiap.hackathon.reservafacil.controller;

import br.com.fiap.hackathon.reservafacil.model.dto.prestador.CadastrarPrestadorRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.PrestadorResponseDTO;
import br.com.fiap.hackathon.reservafacil.service.PrestadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prestadores")
@RequiredArgsConstructor
public class PrestadorController {
    private final PrestadorService prestadorService;

    @GetMapping
    public ResponseEntity<Page<PrestadorResponseDTO>> listarPrestadores(
            @PageableDefault(page = 0, size = 20) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(prestadorService.listarPrestadores(pageable));
    }

    @GetMapping("/localidade")
    public ResponseEntity<List<PrestadorResponseDTO>> buscarPrestadoresPorLocalidade(@RequestParam String localidade) {
        List<PrestadorResponseDTO> listaPrestadoresPorLocalidade = prestadorService.buscarPrestadorPorLocalidade(localidade);
        return ResponseEntity.status(HttpStatus.OK).body(listaPrestadoresPorLocalidade);
    }

    @PostMapping
    public ResponseEntity<PrestadorResponseDTO> cadastrarPrestador(@RequestBody CadastrarPrestadorRequestDTO prestadorRequestDTO) {
        PrestadorResponseDTO prestadorSalvo = prestadorService.cadastrarPrestador(prestadorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(prestadorSalvo);
    }

}
