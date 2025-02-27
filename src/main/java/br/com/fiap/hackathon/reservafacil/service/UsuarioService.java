package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.model.Usuario;

import java.util.Optional;

public interface UsuarioService {
    Usuario salvarUsuario(String cns, String senha, String role);

    Optional<Usuario> obterPorCns(String cns);

    void atualizarSenhaUsuario(String cns, String novaSenha);

    Usuario desativar(String cns);

    Usuario ativar(String cns);
}
