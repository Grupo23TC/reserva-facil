package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.exception.role.RoleNaoEncontradaException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioNaoIguaisException;
import br.com.fiap.hackathon.reservafacil.model.Role;
import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.repository.RoleRepository;
import br.com.fiap.hackathon.reservafacil.repository.UsuarioRepository;
import br.com.fiap.hackathon.reservafacil.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final RoleRepository roleRepository;
    private final SecurityService securityService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario salvarUsuario(String cns, String senha, String role) {
        boolean usuarioExiste = usuarioRepository.existsByCns(cns);

        if(usuarioExiste)
            throw new UsuarioCadastradoException("CNS já cadastrado.");

        Optional<Role> roleOptional = roleRepository.findByAuthority(role);

        if (roleOptional.isEmpty())
            throw new RoleNaoEncontradaException("Não foi possível encontrar a role.");

        Usuario usuario = new Usuario();

        usuario.setCns(cns);
        var senhaCriptografada = passwordEncoder.encode(senha);
        usuario.setSenha(senhaCriptografada);
        usuario.setAtivo(true);
        usuario.getRoles().add(roleOptional.get());

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> obterPorCns(String cns) {
        return usuarioRepository.findByCns(cns);
    }

    @Transactional
    public void atualizarSenhaUsuario(String cns, String novaSenha) {
        Usuario usuario = usuarioRepository.findByCns(cns)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Não foi possível alterar a senha."));

        if(usuariosNaoSaoIguais(cns))
            throw new UsuarioNaoIguaisException("Não foi possível alterar a senha.");

        var senha = passwordEncoder.encode(novaSenha);
        usuario.setSenha(senha);

        usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario desativar(String cns) {
        Usuario usuario = usuarioRepository.findByCns(cns)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Não foi possível desativar usuário."));

        if(usuariosNaoSaoIguais(cns))
            throw new UsuarioNaoIguaisException("Não foi possível desativar usuário.");

        usuario.setAtivo(false);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario ativar(String cns) {
        Usuario usuario = usuarioRepository.findByCns(cns)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Não foi possível ativar usuário."));

        if(usuariosNaoSaoIguais(cns))
            throw new UsuarioNaoIguaisException("Não foi possível ativar usuário.");

        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    private boolean usuariosNaoSaoIguais(String cns) {
        Usuario usuarioLogado = securityService.obterUsuarioLogado();

        return !usuarioLogado.getCns().equals(cns);
    }
}
