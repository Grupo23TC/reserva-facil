package br.com.fiap.hackathon.reservafacil.model.dto.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastrarEnderecoRequest(
        @NotBlank(message = "O logradouro não pode estar em branco")
        String logradouro,
        @NotBlank(message = "O bairro não pode estar em branco")
        String bairro,
        @NotBlank(message = "A cidade não pode estar em branco")
        String cidade,
        @NotBlank(message = "O estado não pode estar em branco")
        @Size(min = 2, max = 2, message = "O estado deve ter exatamente 2 caracteres")
        String estado,
        String complemento
) {
}
