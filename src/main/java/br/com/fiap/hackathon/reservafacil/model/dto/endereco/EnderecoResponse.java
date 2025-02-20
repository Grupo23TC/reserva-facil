package br.com.fiap.hackathon.reservafacil.model.dto.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnderecoResponse(
        String logradouro,
        String bairro,
        String cidade,
        String estado,
        String complemento
) {
}
