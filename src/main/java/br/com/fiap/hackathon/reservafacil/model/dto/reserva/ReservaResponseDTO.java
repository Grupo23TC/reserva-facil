package br.com.fiap.hackathon.reservafacil.model.dto.reserva;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservaResponseDTO (
        UUID id,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime dataReserva,
        String nomeBeneficiario,
        String nomePrestador,
        String nomeMedicamento,
        String orientacaoDocumentos
){
}
