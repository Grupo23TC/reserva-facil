package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.role.RoleNaoEncontradaException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioNaoIguaisException;
import br.com.fiap.hackathon.reservafacil.mapper.EnderecoMapper;
import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.Role;
import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.repository.BeneficiarioRepository;
import br.com.fiap.hackathon.reservafacil.repository.RoleRepository;
import br.com.fiap.hackathon.reservafacil.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BeneficiarioService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final BeneficiarioRepository repository;

    @Transactional
    public void cadastrar(CadastrarBeneficiarioRequest request) {
        Optional<Beneficiario> beneficiarioExiste = repository.findByCns(request.usuario().cns());

        if (beneficiarioExiste.isPresent()) {
            throw new BeneficiarioCadastradoException("Beneficiário já cadastrado");
        }

        Beneficiario beneficiario = criarBeneficiario(request);

        repository.save(beneficiario);
    }

    @Transactional(readOnly = true)
    public Beneficiario buscarPorCns(String cns) {
        Beneficiario beneficiario = repository.findByCns(cns)
                .orElseThrow(() -> new BeneficiarioNaoEncontradoException("Beneficiário não encontrado"));

        if(usuariosNaoSaoIguais(cns)) {
            throw new UsuarioNaoIguaisException("Você não pode ter acesso ou alterar os dados de outros beneficiários");
        }

        return beneficiario;
    }

    @Transactional
    public void ativar(String cns) {
        Beneficiario beneficiario = buscarPorCns(cns);
        beneficiario.setAtivo(true);

        repository.save(beneficiario);
    }

    @Transactional
    public void desativar(String cns) {
        Beneficiario beneficiario = buscarPorCns(cns);
        beneficiario.setAtivo(false);

        repository.save(beneficiario);
    }

    private boolean usuariosNaoSaoIguais(String cns) {
        Usuario usuarioLogado = securityService.obterUsuarioLogado();

        return !usuarioLogado.getCns().equals(cns);
    }

    private Beneficiario criarBeneficiario(CadastrarBeneficiarioRequest request) {
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setCns(request.usuario().cns());
        beneficiario.setNome(request.nome());
        beneficiario.setCpf(request.cpf());
        beneficiario.setTelefone(request.telefone());
        beneficiario.setFaixaEtariaEnum(request.faixaEtaria());
        beneficiario.setEndereco(EnderecoMapper.toEndereco(request.endereco()));
        beneficiario.setGenero(request.genero());
        beneficiario.setTipoMedicamento(request.tipoMedicamento());
        beneficiario.setAtivo(true);

        Usuario usuario = new Usuario();
        usuario.setCns(request.usuario().cns());
        usuario.setSenha(passwordEncoder.encode(request.usuario().senha()));
        usuario.setAtivo(true);

        Role role = roleRepository
                .findByAuthority(request.usuario().role())
                .orElseThrow(() -> new RoleNaoEncontradaException("Role nao encontrado"));

        usuario.getRoles().add(role);
        beneficiario.setUsuario(usuario);

        return beneficiario;
    }
}
