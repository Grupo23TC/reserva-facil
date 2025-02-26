package br.com.fiap.hackathon.reservafacil.model.dto.role;

import java.util.UUID;

public record RoleResponse(
        UUID id,
        String authority
) {
}
