package br.com.fiap.hackathon.reservafacil.model.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record AtualizarSenhaRequest(
        @NotBlank(message = "A senha não pode estar vazia.")
        String novaSenha
) {
}
