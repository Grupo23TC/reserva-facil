package br.com.fiap.hackathon.reservafacil.model.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CadastrarUsuarioRequest(
        @NotBlank(message = "Esse campo não pode estar vazio.")
        @Pattern(regexp = "\\d{15}", message = "O CNS deve conter exatamente 15 dígitos numéricos.")
        String cns,
        @NotBlank(message = "Esse campo não pode estar vazio.")
        String senha,
        @NotBlank(message = "Esse campo não pode estar vazio.")
        String role
) {
}
