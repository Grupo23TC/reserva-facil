package br.com.fiap.hackathon.reservafacil.model.dto.reserva;

import java.time.LocalDateTime;
import java.util.UUID;

public record CadastrarReservaRequestDTO (
        LocalDateTime dataReserva,
        UUID idPrestador,
        UUID idMedicamento
){
}
