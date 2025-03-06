package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.operador.OperadorCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.operador.OperadorNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.AcessoNegadoException;
import br.com.fiap.hackathon.reservafacil.model.*;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.operador.CadastrarOperadorRequest;
import br.com.fiap.hackathon.reservafacil.repository.BeneficiarioRepository;
import br.com.fiap.hackathon.reservafacil.repository.OperadorRepository;
import br.com.fiap.hackathon.reservafacil.repository.PrestadorRepository;
import br.com.fiap.hackathon.reservafacil.repository.RoleRepository;
import br.com.fiap.hackathon.reservafacil.security.SecurityService;
import br.com.fiap.hackathon.reservafacil.service.impl.BeneficiarioServiceImpl;
import br.com.fiap.hackathon.reservafacil.service.impl.OperadorServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.hackathon.reservafacil.util.BeneficiarioUtil.gerarBeneficiario;
import static br.com.fiap.hackathon.reservafacil.util.BeneficiarioUtil.gerarCadastrarBeneficiarioRequest;
import static br.com.fiap.hackathon.reservafacil.util.OperadorUtil.gerarCadastrarOperadorRequest;
import static br.com.fiap.hackathon.reservafacil.util.OperadorUtil.gerarOperador;
import static br.com.fiap.hackathon.reservafacil.util.PrestadorUtil.gerarPrestador;
import static br.com.fiap.hackathon.reservafacil.util.RoleUtil.gerarRole;
import static br.com.fiap.hackathon.reservafacil.util.UsuarilUtil.gerarUsuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OperadorServiceTest {
    private static final String OPERADOR_JA_EXISTE = "Esse operador já foi cadastrado";
    private static final String ACESSO_NEGADO = "Você não pode ter acesso ou alterar os dados de outros operadores";
    private static final String OPERADOR_NAO_ENCONTRADO = "Operador não encontrado";

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PrestadorRepository prestadorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityService securityService;

    @Mock
    private OperadorRepository repository;

    @InjectMocks
    private OperadorServiceImpl service;

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
    class CadastrarOperador {
        @Test
        void deveCadastrarOperador() {
            CadastrarOperadorRequest request = gerarCadastrarOperadorRequest();
            Prestador prestador = gerarPrestador();
            Role role = gerarRole();

            when(repository.existsByCns(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("senha-criptografada");
            when(roleRepository.findByAuthority(anyString())).thenReturn(Optional.of(role));
            when(prestadorRepository.findById(any(UUID.class))).thenReturn(Optional.of(prestador));
            when(repository.save(any(Operador.class))).thenAnswer(i -> i.getArgument(0));

            Operador operadorSalvo = service.cadastrar(request);

            assertThat(operadorSalvo)
                    .isNotNull()
                    .isInstanceOf(Operador.class)
                    .extracting(Operador::getCns, Operador::getCargo, Operador::getNome)
                    .containsExactly(operadorSalvo.getCns(), operadorSalvo.getCargo(), operadorSalvo.getNome());

            verify(repository, times(1)).save(any(Operador.class));
            verify(roleRepository, times(1)).findByAuthority(anyString());
            verify(passwordEncoder, times(1)).encode(anyString());
            verify(repository, times(1)).existsByCns(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarOperador_CnsJaCadastrado() {
            CadastrarOperadorRequest request = gerarCadastrarOperadorRequest();

            when(repository.existsByCns(anyString())).thenReturn(true);

            assertThatThrownBy(() -> service.cadastrar(request))
                    .isNotNull()
                    .isInstanceOf(OperadorCadastradoException.class)
                    .hasMessage(OPERADOR_JA_EXISTE);

            verify(repository, never()).save(any(Operador.class));
            verify(roleRepository, never()).findByAuthority(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(repository, times(1)).existsByCns(anyString());
        }
    }

    @Nested
    class BuscarOperador {
        @Test
        void deveBuscarPorCns() {
            Operador operador = gerarOperador();
            Usuario usuario = gerarUsuario();

            when(repository.findByCns(anyString())).thenReturn(Optional.of(operador));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);

            Operador response = service.buscarPorCns("654987123065482");

            assertThat(response)
                .isNotNull()
                .isInstanceOf(Operador.class)
                .extracting(Operador::getCns, Operador::getCargo, Operador::getNome)
                .containsExactly(operador.getCns(), operador.getCargo(), operador.getNome());

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPorCns_CnsNaoEncontrado() {
            when(repository.findByCns(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorCns("654987123065482"))
                    .isNotNull()
                    .isInstanceOf(OperadorNaoEncontradoException.class)
                    .hasMessage(OPERADOR_NAO_ENCONTRADO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, never()).obterUsuarioLogado();
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPorCns_AcessoNegado() {
            Operador operador = gerarOperador();
            Usuario usuario = gerarUsuario("123456789012345");

            when(repository.findByCns(anyString())).thenReturn(Optional.of(operador));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);

            assertThatThrownBy(() -> service.buscarPorCns("654987123065482"))
                    .isNotNull()
                    .isInstanceOf(AcessoNegadoException.class)
                    .hasMessage(ACESSO_NEGADO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
        }

    }
    @Nested
    class AtivarOperador {
        @Test
        void deveAtivarOperador() {
            Operador operador = gerarOperador();
            Usuario usuario = gerarUsuario();

            when(repository.findByCns(anyString())).thenReturn(Optional.of(operador));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);
            when(repository.save(any(Operador.class))).thenAnswer(i -> i.getArgument(0));

            Operador response = service.ativar("654987123065482");

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Operador.class)
                    .extracting(Operador::getCns, Operador::getCargo, Operador::getAtivo)
                    .containsExactly(operador.getCns(), operador.getCargo(), true);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(repository, times(1)).save(any(Operador.class));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarOperador_CnsNaoEncontrado() {
            when(repository.findByCns(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.ativar("654987123065481"))
                    .isNotNull()
                    .isInstanceOf(OperadorNaoEncontradoException.class)
                    .hasMessage(OPERADOR_NAO_ENCONTRADO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, never()).obterUsuarioLogado();
            verify(repository, never()).save(any(Operador.class));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarOperador_AcessoNegado() {
            Operador operador = gerarOperador();
            Usuario usuario = gerarUsuario("123456789012345");

            when(repository.findByCns(anyString())).thenReturn(Optional.of(operador));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);

            assertThatThrownBy(() -> service.ativar("654987123065482"))
                    .isNotNull()
                    .isInstanceOf(AcessoNegadoException.class)
                    .hasMessage(ACESSO_NEGADO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(repository, never()).save(any(Operador.class));
        }
    }

    @Nested
    class DesativarOperador {
        @Test
        void deveDesativarOperador() {
            Operador operador = gerarOperador();
            Usuario usuario = gerarUsuario();

            when(repository.findByCns(anyString())).thenReturn(Optional.of(operador));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);
            when(repository.save(any(Operador.class))).thenAnswer(i -> i.getArgument(0));

            Operador response = service.desativar("654987123065482");

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Operador.class)
                    .extracting(Operador::getCns, Operador::getCargo, Operador::getAtivo)
                    .containsExactly(operador.getCns(), operador.getCargo(), false);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(repository, times(1)).save(any(Operador.class));
        }

        @Test
        void deveGerarExcecao_QuandoDesativarOperador_CnsNaoEncontrado() {
            when(repository.findByCns(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.desativar("654987123065481"))
                    .isNotNull()
                    .isInstanceOf(OperadorNaoEncontradoException.class)
                    .hasMessage(OPERADOR_NAO_ENCONTRADO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, never()).obterUsuarioLogado();
            verify(repository, never()).save(any(Operador.class));
        }

        @Test
        void deveGerarExcecao_QuandoDesativarOperador_AcessoNegado() {
            Operador operador = gerarOperador();
            Usuario usuario = gerarUsuario("123456789012345");

            when(repository.findByCns(anyString())).thenReturn(Optional.of(operador));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);

            assertThatThrownBy(() -> service.desativar("654987123065482"))
                    .isNotNull()
                    .isInstanceOf(AcessoNegadoException.class)
                    .hasMessage(ACESSO_NEGADO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(repository, never()).save(any(Operador.class));
        }
    }
}
