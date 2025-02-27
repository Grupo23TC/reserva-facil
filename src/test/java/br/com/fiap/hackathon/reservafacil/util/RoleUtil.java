package br.com.fiap.hackathon.reservafacil.util;

import br.com.fiap.hackathon.reservafacil.model.Role;

import java.util.UUID;

public class RoleUtil {
    public static Role gerarRole() {
        return new Role(UUID.randomUUID(), "TESTE");
    }
}
