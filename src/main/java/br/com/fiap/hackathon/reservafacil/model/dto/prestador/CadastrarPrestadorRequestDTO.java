package br.com.fiap.hackathon.reservafacil.model.dto.prestador;

import br.com.fiap.hackathon.reservafacil.model.enums.TipoPrestadorEnum;

public record CadastrarPrestadorRequestDTO (
        String nome,
        String nomeFantasia,
        String endereco,
        TipoPrestadorEnum tipoPrestadorEnum
    ){
}
