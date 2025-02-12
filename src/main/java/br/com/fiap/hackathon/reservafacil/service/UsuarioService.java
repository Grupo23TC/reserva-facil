package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void salvarUsuario(Usuario usuario) {
        var senha = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senha);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario obterPorCns(String cns) {
        return usuarioRepository.findByCns(cns);
    }
    
}
