package br.com.fiap.hackathon.reservafacil.security;

import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.service.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final UsuarioServiceImpl usuarioService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String cns = authentication.getName();
        String senhaDigitada = authentication.getCredentials().toString();

        Usuario usuario = usuarioService.obterPorCns(cns)
                .orElseThrow(this::getErroUsuarioNaoEncontrado);

        boolean senhasBatem = passwordEncoder.matches(senhaDigitada, usuario.getSenha());

        if(senhasBatem) return new CustomAuthentication(usuario);

        throw getErroUsuarioNaoEncontrado();
    }

    private UsernameNotFoundException getErroUsuarioNaoEncontrado() {
        return new UsernameNotFoundException("Usuário e/ou senha incorretos");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
