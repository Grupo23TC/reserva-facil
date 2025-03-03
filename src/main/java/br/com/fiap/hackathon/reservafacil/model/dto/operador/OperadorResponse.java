package br.com.fiap.hackathon.reservafacil.model.dto.operador;

import br.com.fiap.hackathon.reservafacil.model.dto.prestador.PrestadorResponseDTO;

public record OperadorResponse(
        String cns,
        String nome,
        String cargo,
        PrestadorResponseDTO prestador,
        Boolean ativo
) {
}
