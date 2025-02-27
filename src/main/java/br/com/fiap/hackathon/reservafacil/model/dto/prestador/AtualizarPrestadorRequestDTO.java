package br.com.fiap.hackathon.reservafacil.model.dto.prestador;

import br.com.fiap.hackathon.reservafacil.model.Endereco;
import br.com.fiap.hackathon.reservafacil.model.enums.TipoPrestadorEnum;

public record AtualizarPrestadorRequestDTO(

        String nome,
        String nomeFantasia,
        Endereco endereco,
        TipoPrestadorEnum tipoPrestador
) {
}
