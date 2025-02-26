package br.com.fiap.hackathon.reservafacil.mapper;

import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.model.dto.role.RoleResponse;
import br.com.fiap.hackathon.reservafacil.model.dto.usuario.UsuarioResponse;

import java.util.stream.Collectors;

public class UsuarioMapper {
    public static UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getCns(),
                usuario.getAtivo(),
                usuario.getRoles()
                        .stream()
                        .map(r -> new RoleResponse(r.getId(), r.getAuthority()))
                        .collect(Collectors.toSet())
        );
    }
}
