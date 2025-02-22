package br.com.fiap.hackathon.reservafacil.model.dto.usuario;

import br.com.fiap.hackathon.reservafacil.model.dto.role.RoleResponse;

import java.util.Set;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String cns,
        Boolean ativo,
        Set<RoleResponse> roles
) {
}
