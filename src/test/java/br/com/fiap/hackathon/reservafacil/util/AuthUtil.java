package br.com.fiap.hackathon.reservafacil.util;

import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.security.CustomAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static br.com.fiap.hackathon.reservafacil.util.UsuarioUtil.gerarUsuario;

public class AuthUtil {
    public static void autenticar(String cns) {
        Usuario usuario = gerarUsuario(cns);
        Authentication authentication = new CustomAuthentication(usuario);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
