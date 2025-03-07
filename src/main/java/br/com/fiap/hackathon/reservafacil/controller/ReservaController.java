package br.com.fiap.hackathon.reservafacil.controller;

import br.com.fiap.hackathon.reservafacil.model.dto.reserva.CadastrarReservaRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.reserva.ReservaResponseDTO;
import br.com.fiap.hackathon.reservafacil.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService service;

    @PostMapping
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ReservaResponseDTO> cadastrar(@Valid @RequestBody CadastrarReservaRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrarReserva(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'OPERADOR')")
    public ResponseEntity<ReservaResponseDTO> buscar(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.buscarReserva(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<ReservaResponseDTO>> listar() {
        return ResponseEntity.status(HttpStatus.OK).body(service.listarReservas());
    }

    @GetMapping("/beneficiario/{cns}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasBeneficiarios(@PathVariable String cns) {
        return ResponseEntity.status(HttpStatus.OK).body(service.listarReservasBeneficiario(cns));
    }

    @GetMapping("/prestador/{prestadorId}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasPrestador(@PathVariable UUID prestadorId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.listarReservasPrestador(prestadorId));
    }

    @GetMapping("/horariosDisponiveis")
    @PreAuthorize("hasAnyRole('PACIENTE', 'OPERADOR')")
    public ResponseEntity<List<String>> listarHorariosDisponiveisPeriodoPrestador(@RequestParam UUID prestadorId,
                                                                                         @RequestParam LocalDateTime dataInicio,
                                                                                            @RequestParam LocalDateTime dataFim) {
        return ResponseEntity.status(HttpStatus.OK).body(service.listarHorariosDisponiveisPeriodoPrestador(prestadorId, dataInicio, dataFim));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'OPERADOR')")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        service.deletarReserva(id);
        return ResponseEntity.noContent().build();
    }
}
