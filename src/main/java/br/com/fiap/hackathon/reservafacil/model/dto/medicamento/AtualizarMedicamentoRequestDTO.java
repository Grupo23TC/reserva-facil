package br.com.fiap.hackathon.reservafacil.model.dto.medicamento;

import java.time.LocalDate;


public record AtualizarMedicamentoRequestDTO(

        String nome,
        Integer quantidade,
        LocalDate validade
) {
}
