package br.com.fiap.hackathon.reservafacil.security;

import br.com.fiap.hackathon.reservafacil.model.Role;
import br.com.fiap.hackathon.reservafacil.model.Usuario;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class CustomAuthentication implements Authentication {
    private final Usuario usuario;

    @Override
    public Collection<Role> getAuthorities() {
        return usuario.getRoles();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return usuario;
    }

    @Override
    public Object getPrincipal() {
        return usuario;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return usuario.getCns();
    }
}
