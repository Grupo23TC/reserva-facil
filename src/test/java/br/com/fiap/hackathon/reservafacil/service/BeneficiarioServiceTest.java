package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioNaoIguaisException;
import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.Role;
import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.repository.BeneficiarioRepository;
import br.com.fiap.hackathon.reservafacil.repository.RoleRepository;
import br.com.fiap.hackathon.reservafacil.security.SecurityService;
import br.com.fiap.hackathon.reservafacil.service.impl.BeneficiarioServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static br.com.fiap.hackathon.reservafacil.Util.BeneficiarioUtil.gerarBeneficiario;
import static br.com.fiap.hackathon.reservafacil.Util.BeneficiarioUtil.gerarCadastrarBeneficiarioRequest;
import static br.com.fiap.hackathon.reservafacil.Util.RoleUtil.gerarRole;
import static br.com.fiap.hackathon.reservafacil.Util.UsuarilUtil.gerarUsuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BeneficiarioServiceTest {
    private static final String BENEFICIARIO_JA_EXISTE = "Beneficiário já cadastrado";
    private static final String ACESSO_NEGADO = "Você não pode ter acesso ou alterar os dados de outros beneficiários";
    private static final String BENEFICIARIO_NAO_ENCONTRADO = "Beneficiário não encontrado";

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityService securityService;

    @Mock
    private BeneficiarioRepository repository;

    @InjectMocks
    private BeneficiarioServiceImpl service;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class CadastrarBeneficiario {
        @Test
        void deveCadastrarBeneficiario() {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest();
            Role role = gerarRole();

            when(repository.existsByCns(anyString())).thenReturn(false);
            when(repository.existsByCpf(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("senha-criptografada");
            when(roleRepository.findByAuthority(anyString())).thenReturn(Optional.of(role));
            when(repository.save(any(Beneficiario.class))).thenAnswer(i -> i.getArgument(0));

            Beneficiario beneficiarioSalvo = service.cadastrar(request);

            assertThat(beneficiarioSalvo)
                    .isNotNull()
                    .isInstanceOf(Beneficiario.class)
                    .extracting(Beneficiario::getCns, Beneficiario::getCpf, Beneficiario::getNome)
                    .containsExactly(beneficiarioSalvo.getCns(), beneficiarioSalvo.getCpf(), beneficiarioSalvo.getNome());

            verify(repository, times(1)).save(any(Beneficiario.class));
            verify(roleRepository, times(1)).findByAuthority(anyString());
            verify(passwordEncoder, times(1)).encode(anyString());
            verify(repository, times(1)).existsByCns(anyString());
            verify(repository, times(1)).existsByCpf(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarBeneficiario_CnsJaCadastrado() {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest();

            when(repository.existsByCns(anyString())).thenReturn(true);
            when(repository.existsByCpf(anyString())).thenReturn(false);

            assertThatThrownBy(() -> service.cadastrar(request))
                    .isNotNull()
                    .isInstanceOf(BeneficiarioCadastradoException.class)
                    .hasMessage(BENEFICIARIO_JA_EXISTE);

            verify(repository, never()).save(any(Beneficiario.class));
            verify(roleRepository, never()).findByAuthority(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(repository, times(1)).existsByCns(anyString());
            verify(repository, times(1)).existsByCpf(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarBeneficiario_CpfJaCadastrado() {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest();

            when(repository.existsByCns(anyString())).thenReturn(false);
            when(repository.existsByCpf(anyString())).thenReturn(true);

            assertThatThrownBy(() -> service.cadastrar(request))
                    .isNotNull()
                    .isInstanceOf(BeneficiarioCadastradoException.class)
                    .hasMessage(BENEFICIARIO_JA_EXISTE);

            verify(repository, never()).save(any(Beneficiario.class));
            verify(roleRepository, never()).findByAuthority(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(repository, times(1)).existsByCns(anyString());
            verify(repository, times(1)).existsByCpf(anyString());
        }
    }

    @Nested
    class BuscarBeneficiario {
        @Test
        void deveBuscarPorCns() {
            Beneficiario beneficiario = gerarBeneficiario();
            Usuario usuario = gerarUsuario();

            when(repository.findByCns(anyString())).thenReturn(Optional.of(beneficiario));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);

            Beneficiario response = service.buscarPorCns("654987123065482");

            assertThat(response)
                .isNotNull()
                .isInstanceOf(Beneficiario.class)
                .extracting(Beneficiario::getCns, Beneficiario::getCpf, Beneficiario::getNome)
                .containsExactly(beneficiario.getCns(), beneficiario.getCpf(), beneficiario.getNome());

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPorCns_CnsNaoEncontrado() {
            when(repository.findByCns(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorCns("654987123065482"))
                    .isNotNull()
                    .isInstanceOf(BeneficiarioNaoEncontradoException.class)
                    .hasMessage(BENEFICIARIO_NAO_ENCONTRADO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, never()).obterUsuarioLogado();
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPorCns_AcessoNegado() {
            Beneficiario beneficiario = gerarBeneficiario();
            Usuario usuario = gerarUsuario("123456789012345");

            when(repository.findByCns(anyString())).thenReturn(Optional.of(beneficiario));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);

            assertThatThrownBy(() -> service.buscarPorCns("654987123065482"))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoIguaisException.class)
                    .hasMessage(ACESSO_NEGADO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
        }

    }
    @Nested
    class AtivarBeneficiario {
        @Test
        void deveAtivarBeneficiario() {
            Beneficiario beneficiario = gerarBeneficiario();
            Usuario usuario = gerarUsuario();

            when(repository.findByCns(anyString())).thenReturn(Optional.of(beneficiario));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);
            when(repository.save(any(Beneficiario.class))).thenAnswer(i -> i.getArgument(0));

            Beneficiario response = service.ativar("654987123065482");

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Beneficiario.class)
                    .extracting(Beneficiario::getCns, Beneficiario::getCpf, Beneficiario::getAtivo)
                    .containsExactly(beneficiario.getCns(), beneficiario.getCpf(), true);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(repository, times(1)).save(any(Beneficiario.class));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarBeneficiario_CnsNaoEncontrado() {
            when(repository.findByCns(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.ativar("654987123065481"))
                    .isNotNull()
                    .isInstanceOf(BeneficiarioNaoEncontradoException.class)
                    .hasMessage(BENEFICIARIO_NAO_ENCONTRADO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, never()).obterUsuarioLogado();
            verify(repository, never()).save(any(Beneficiario.class));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarBeneficiario_AcessoNegado() {
            Beneficiario beneficiario = gerarBeneficiario();
            Usuario usuario = gerarUsuario("123456789012345");

            when(repository.findByCns(anyString())).thenReturn(Optional.of(beneficiario));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);

            assertThatThrownBy(() -> service.ativar("654987123065482"))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoIguaisException.class)
                    .hasMessage(ACESSO_NEGADO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(repository, never()).save(any(Beneficiario.class));
        }
    }

    @Nested
    class DesativarBeneficiario {
        @Test
        void deveDesativarBeneficiario() {
            Beneficiario beneficiario = gerarBeneficiario();
            Usuario usuario = gerarUsuario();

            when(repository.findByCns(anyString())).thenReturn(Optional.of(beneficiario));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);
            when(repository.save(any(Beneficiario.class))).thenAnswer(i -> i.getArgument(0));

            Beneficiario response = service.desativar("654987123065482");

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Beneficiario.class)
                    .extracting(Beneficiario::getCns, Beneficiario::getCpf, Beneficiario::getAtivo)
                    .containsExactly(beneficiario.getCns(), beneficiario.getCpf(), false);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(repository, times(1)).save(any(Beneficiario.class));
        }

        @Test
        void deveGerarExcecao_QuandoDesativarBeneficiario_CnsNaoEncontrado() {
            when(repository.findByCns(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.desativar("654987123065481"))
                    .isNotNull()
                    .isInstanceOf(BeneficiarioNaoEncontradoException.class)
                    .hasMessage(BENEFICIARIO_NAO_ENCONTRADO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, never()).obterUsuarioLogado();
            verify(repository, never()).save(any(Beneficiario.class));
        }

        @Test
        void deveGerarExcecao_QuandoDesativarBeneficiario_UmUsuarioTentandoAtivarOutroUsuario() {
            Beneficiario beneficiario = gerarBeneficiario();
            Usuario usuario = gerarUsuario("123456789012345");

            when(repository.findByCns(anyString())).thenReturn(Optional.of(beneficiario));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);

            assertThatThrownBy(() -> service.desativar("654987123065482"))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoIguaisException.class)
                    .hasMessage(ACESSO_NEGADO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(repository, never()).save(any(Beneficiario.class));
        }
    }
}
