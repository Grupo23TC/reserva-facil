package br.com.fiap.hackathon.reservafacil.model.dto.medicamento;

import br.com.fiap.hackathon.reservafacil.model.enums.TipoMedicamentoEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CadastrarMedicamentoRequestDTO(

        String nome,
        TipoMedicamentoEnum tipo,
        Integer quantidade,
        LocalDate validade,
        String lote,
        List<String> documentos
) {
}
