package br.com.fiap.hackathon.reservafacil.model.dto.prestador;

import br.com.fiap.hackathon.reservafacil.model.Endereco;
import br.com.fiap.hackathon.reservafacil.model.enums.TipoPrestadorEnum;

import java.util.UUID;


public record PrestadorResponseDTO(
        UUID id,
        String nomeFantasia,
        Endereco prestadorEndereco,
        TipoPrestadorEnum tipoPrestador
    ){
}
