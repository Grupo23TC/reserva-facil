package br.com.fiap.hackathon.reservafacil.model.dto.operador;

import br.com.fiap.hackathon.reservafacil.model.dto.usuario.CadastrarUsuarioRequest;
import jakarta.validation.Valid;

import java.util.UUID;

public record CadastrarOperadorRequest(
        @Valid CadastrarUsuarioRequest usuario,
        String nome,
        String cargo,
        UUID prestadorId
) {
}
