package br.com.fiap.hackathon.reservafacil.model.dto.prestador;

import br.com.fiap.hackathon.reservafacil.model.enums.TipoPrestadorEnum;


public record PrestadorResponseDTO (
        String nomeFantasia,
        String endereco,
        TipoPrestadorEnum tipoPrestadorEnum
    ){
}
