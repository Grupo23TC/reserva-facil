package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.model.dto.reserva.CadastrarReservaRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.reserva.ReservaResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservaService {

    ReservaResponseDTO cadastrarReserva(CadastrarReservaRequestDTO dto);

    ReservaResponseDTO buscarReserva(UUID id);

    List<ReservaResponseDTO> listarReservas();

    void deletarReserva(UUID id);

    List<String> listarHorariosDisponiveisPeriodoPrestador(UUID prestadorId, LocalDateTime dataInicial, LocalDateTime dataFinal);
}
