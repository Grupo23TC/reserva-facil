package br.com.fiap.hackathon.reservafacil.Util;

import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.model.dto.usuario.AtualizarSenhaRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.usuario.CadastrarUsuarioRequest;

import java.util.Set;
import java.util.UUID;

import static br.com.fiap.hackathon.reservafacil.Util.RoleUtil.gerarRole;

public class UsuarilUtil {
    public static CadastrarUsuarioRequest gerarCadastrarUsuarioRequest() {
        return new CadastrarUsuarioRequest(
                "654987123065482",
                "123",
                "PACIENTE"
        );
    }

    public static CadastrarUsuarioRequest gerarCadastrarUsuarioRequest(String cns) {
        return new CadastrarUsuarioRequest(
                cns,
                "123",
                "PACIENTE"
        );
    }

    public static CadastrarUsuarioRequest gerarCadastrarUsuarioRequest(String cns, String role) {
        return new CadastrarUsuarioRequest(
                cns,
                "123",
                role
        );
    }

    public static Usuario gerarUsuario() {
        return new Usuario(
                UUID.randomUUID(),
                "654987123065482",
                "123",
                true,
                Set.of(gerarRole())
        );
    }

    public static Usuario gerarUsuario(String cns) {
        return new Usuario(
                UUID.randomUUID(),
                cns,
                "123",
                true,
                Set.of(gerarRole())
        );
    }

    public static Usuario gerarUsuario(boolean ativo) {
        return new Usuario(
                UUID.randomUUID(),
                "654987123065482",
                "123",
                ativo,
                Set.of(gerarRole())
        );
    }

    public static AtualizarSenhaRequest gerarAtualizarSenhaRequest() {
        return new AtualizarSenhaRequest("123");
    }
}
