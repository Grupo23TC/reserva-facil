package br.com.fiap.hackathon.reservafacil.service.impl;

import br.com.fiap.hackathon.reservafacil.exception.operador.OperadorCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.operador.OperadorNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.prestador.PrestadorNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.role.RoleNaoEncontradaException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.AcessoNegadoException;
import br.com.fiap.hackathon.reservafacil.model.*;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.operador.CadastrarOperadorRequest;
import br.com.fiap.hackathon.reservafacil.repository.OperadorRepository;
import br.com.fiap.hackathon.reservafacil.repository.PrestadorRepository;
import br.com.fiap.hackathon.reservafacil.repository.RoleRepository;
import br.com.fiap.hackathon.reservafacil.security.SecurityService;
import br.com.fiap.hackathon.reservafacil.service.OperadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperadorServiceImpl implements OperadorService {
    private final OperadorRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final PrestadorRepository prestadorRepository;

    @Override
    @Transactional
    public Operador cadastrar(CadastrarOperadorRequest request) {
        boolean operadorExisteCns = repository.existsByCns(request.usuario().cns());

        if(operadorExisteCns) {
            throw new OperadorCadastradoException("Esse operador já foi cadastrado");
        }

        Operador operador = criarOperador(request);
        return repository.save(operador);
    }

    @Override
    @Transactional(readOnly = true)
    public Operador buscarPorCns(String cns) {
        Operador operador = repository.findByCns(cns).orElseThrow(() -> new OperadorNaoEncontradoException("Operador não encontrado"));

        if(usuariosNaoSaoIguais(cns)) {
            throw new AcessoNegadoException("Você não pode ter acesso ou alterar os dados de outros operadores");
        }

        return operador;
    }

    @Override
    @Transactional
    public Operador ativar(String cns) {
        Operador operador = buscarPorCns(cns);
        operador.setAtivo(true);
        return repository.save(operador);
    }

    @Override
    @Transactional
    public Operador desativar(String cns) {
        Operador operador = buscarPorCns(cns);
        operador.setAtivo(false);
        return repository.save(operador);
    }

    private boolean usuariosNaoSaoIguais(String cns) {
        Usuario usuarioLogado = securityService.obterUsuarioLogado();

        return !usuarioLogado.getCns().equals(cns);
    }

    private Operador criarOperador(CadastrarOperadorRequest request) {
        Operador operador = new Operador();
        operador.setCns(request.usuario().cns());
        operador.setCargo(request.cargo());
        operador.setAtivo(true);
        operador.setNome(request.nome());

        Usuario usuario = new Usuario();
        usuario.setCns(request.usuario().cns());
        usuario.setSenha(passwordEncoder.encode(request.usuario().senha()));
        usuario.setAtivo(true);

        Role role = roleRepository
                .findByAuthority(request.usuario().role())
                .orElseThrow(() -> new RoleNaoEncontradaException("Role nao encontrado"));

        usuario.getRoles().add(role);
        operador.setUsuario(usuario);


        Prestador prestador = prestadorRepository.findById(request.prestadorId())
                .orElseThrow(() -> new PrestadorNaoEncontradoException("Prestador não encontrado"));
        operador.setPrestador(prestador);

        return operador;
    }
}
