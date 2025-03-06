package br.com.fiap.hackathon.reservafacil.service.impl;

import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.role.RoleNaoEncontradaException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.AcessoNegadoException;
import br.com.fiap.hackathon.reservafacil.mapper.EnderecoMapper;
import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.Role;
import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.repository.BeneficiarioRepository;
import br.com.fiap.hackathon.reservafacil.repository.RoleRepository;
import br.com.fiap.hackathon.reservafacil.security.SecurityService;
import br.com.fiap.hackathon.reservafacil.service.BeneficiarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BeneficiarioServiceImpl implements BeneficiarioService {
    private static final String BENEFICIARIO_JA_EXISTE = "Beneficiário já cadastrado";

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final BeneficiarioRepository repository;

    @Transactional
    public Beneficiario cadastrar(CadastrarBeneficiarioRequest request) {
        boolean beneficiarioExisteCns = repository.existsByCns(request.usuario().cns());
        boolean beneficiarioExisteCpf = repository.existsByCpf(request.cpf());

        if (beneficiarioExisteCns) {
            throw new BeneficiarioCadastradoException(BENEFICIARIO_JA_EXISTE);
        }

        if (beneficiarioExisteCpf) {
            throw new BeneficiarioCadastradoException(BENEFICIARIO_JA_EXISTE);

        }

        Beneficiario beneficiario = criarBeneficiario(request);

        return repository.save(beneficiario);
    }

    @Transactional(readOnly = true)
    public Beneficiario buscarPorCns(String cns) {
        Beneficiario beneficiario = repository.findByCns(cns)
                .orElseThrow(() -> new BeneficiarioNaoEncontradoException("Beneficiário não encontrado"));

        if(usuariosNaoSaoIguais(cns)) {
            throw new AcessoNegadoException("Você não pode ter acesso ou alterar os dados de outros beneficiários");
        }

        return beneficiario;
    }

    @Transactional
    public Beneficiario ativar(String cns) {
        Beneficiario beneficiario = buscarPorCns(cns);
        beneficiario.setAtivo(true);

        return repository.save(beneficiario);
    }

    @Transactional
    public Beneficiario desativar(String cns) {
        Beneficiario beneficiario = buscarPorCns(cns);
        beneficiario.setAtivo(false);

        return repository.save(beneficiario);
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
