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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static br.com.fiap.hackathon.reservafacil.Util.RoleUtil.gerarRole;
import static br.com.fiap.hackathon.reservafacil.Util.UsuarilUtil.gerarUsuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {
    private static final String USUARIO_JA_CADASTRADO = "CNS já cadastrado.";
    private static final String ROLE_NAO_ENCONTRADA = "Não foi possível encontrar a role.";
    private static final String NAO_FOI_POSSIVEL_ALTERAR_A_SENHA = "Não foi possível alterar a senha.";
    private static final String NAO_FOI_POSSIVEL_DESATIVAR_USUARIO = "Não foi possível desativar usuário.";
    private static final String NAO_FOI_POSSIVEL_ATIVAR_USUARIO = "Não foi possível ativar usuário.";

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

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
    class CadastraUsuario {
        @Test
        void deveCadastrarUsuario() {
            String cns = "12345678912346";
            String senha = "123";
            String senhaCriptografada = "senha-criptografada";
            String roleNome = "PACIENTE";
            Role role = gerarRole();

            when(repository.existsByCns(anyString())).thenReturn(false);
            when(roleRepository.findByAuthority(anyString())).thenReturn(Optional.of(role));
            when(passwordEncoder.encode(anyString())).thenReturn(senhaCriptografada);
            when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

            Usuario response = service.salvarUsuario(cns, senha, roleNome);

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Usuario.class)
                    .extracting(Usuario::getCns, Usuario::getSenha)
                    .containsExactly(cns, senhaCriptografada);

            assertThat(
                    response
                            .getRoles()
                            .stream()
                            .map(Role::getAuthority)
                            .anyMatch(authority -> authority.equals(role.getAuthority()))
            )
            .isTrue();

            verify(repository, times(1)).save(any(Usuario.class));
            verify(roleRepository, times(1)).findByAuthority(anyString());
            verify(passwordEncoder, times(1)).encode(anyString());
            verify(repository, times(1)).existsByCns(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarUsuario_CnsJaCadastrado() {
            String cns = "12345678912346";
            String senha = "123";
            String roleNome = "PACIENTE";

            when(repository.existsByCns(anyString())).thenReturn(true);

            assertThatThrownBy(() -> service.salvarUsuario(cns, senha, roleNome))
                    .isNotNull()
                    .isInstanceOf(UsuarioCadastradoException.class)
                    .hasMessageContaining(USUARIO_JA_CADASTRADO);

            verify(repository, never()).save(any(Usuario.class));
            verify(roleRepository, never()).findByAuthority(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(repository, times(1)).existsByCns(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarUsuario_RoleNaoEncontrada() {
            String cns = "12345678912346";
            String senha = "123";
            String roleNome = "PACIENTE";

            when(repository.existsByCns(anyString())).thenReturn(false);
            when(roleRepository.findByAuthority(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.salvarUsuario(cns, senha, roleNome))
                    .isNotNull()
                    .isInstanceOf(RoleNaoEncontradaException.class)
                    .hasMessageContaining(ROLE_NAO_ENCONTRADA);

            verify(repository, never()).save(any(Usuario.class));
            verify(roleRepository, times(1)).findByAuthority(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(repository, times(1)).existsByCns(anyString());
        }
    }

    @Nested
    class BuscaUsuario {
        @Test
        void deveObterUsuarioPorCns() {
            Usuario usuario = gerarUsuario();
            String cns = "654987123065482";

            when(repository.findByCns(anyString())).thenReturn(Optional.of(usuario));

            Optional<Usuario> usuarioOptional = service.obterPorCns(cns);

            assertThat(usuarioOptional.isPresent()).isTrue();
            assertThat(usuarioOptional.get().getCns()).isEqualTo(cns);

            verify(repository, times(1)).findByCns(anyString());
        }

        @Test
        void deveObterUsuarioPorCns_UsuarioNaoEncontrado() {
            String cns = "654987123065482";

            when(repository.findByCns(anyString())).thenReturn(Optional.empty());

            Optional<Usuario> usuarioOptional = service.obterPorCns(cns);

            assertThat(usuarioOptional.isEmpty()).isTrue();

            verify(repository, times(1)).findByCns(anyString());
        }
    }

    @Nested
    class AtualizarUsuario {
        @Test
        void deveAtualizaSenhaUsuario() {
            String cns = "654987123065482";
            String novaSenha = "123456";
            String novaSenhaCriptografada = "nova-senha-criptografada";
            Usuario usuario = gerarUsuario();

            when(repository.findByCns(anyString())).thenReturn(Optional.of(usuario));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);
            when(passwordEncoder.encode(anyString())).thenReturn(novaSenhaCriptografada);
            when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

            service.atualizarSenhaUsuario(cns, novaSenha);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(passwordEncoder, times(1)).encode(anyString());
            verify(repository, times(1)).save(any(Usuario.class));
        }

        @Test
        void deveGerarExcecao_QuandoAtualizarSenhaUsuario_CnsNaoEncontrado() {
            String cns = "654987123065482";
            String novaSenha = "123456";

            when(repository.findByCns(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.atualizarSenhaUsuario(cns, novaSenha))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoEncontradoException.class)
                    .hasMessage(NAO_FOI_POSSIVEL_ALTERAR_A_SENHA);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, never()).obterUsuarioLogado();
            verify(passwordEncoder, never()).encode(anyString());
            verify(repository, never()).save(any(Usuario.class));
        }

        @Test
        void deveGerarExcecao_QuandoAtualizarSenhaUsuario_AcessoNegado() {
            String cns = "654987123065482";
            String novaSenha = "123456";
            Usuario usuario = gerarUsuario(cns);
            Usuario usuarioLogado = gerarUsuario("123456789012345");

            when(repository.findByCns(anyString())).thenReturn(Optional.of(usuario));
            when(securityService.obterUsuarioLogado()).thenReturn(usuarioLogado);

            assertThatThrownBy(() -> service.atualizarSenhaUsuario(cns, novaSenha))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoIguaisException.class)
                    .hasMessage(NAO_FOI_POSSIVEL_ALTERAR_A_SENHA);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(passwordEncoder, never()).encode(anyString());
            verify(repository, never()).save(any(Usuario.class));
        }
    }

    @Nested
    class DesativarUsuario {
        @Test
        void deveDesativarUsuario() {
            String cns = "654987123065482";
            Usuario usuario = gerarUsuario();

            when(repository.findByCns(anyString())).thenReturn(Optional.of(usuario));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);
            when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

            Usuario response = service.desativar(cns);

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Usuario.class)
                    .extracting(Usuario::getCns, Usuario::getAtivo)
                    .containsExactly(cns, false);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(repository, times(1)).save(any(Usuario.class));
        }

        @Test
        void deveGerarExcecao_QuandoDesativarUsuario_CnsNaoEncontrado() {
            String cns = "654987123065482";

            when(repository.findByCns(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.desativar(cns))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoEncontradoException.class)
                    .hasMessage(NAO_FOI_POSSIVEL_DESATIVAR_USUARIO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, never()).obterUsuarioLogado();
            verify(repository, never()).save(any(Usuario.class));
        }

        @Test
        void deveGerarExcecao_QUandoDesativarUsuario_UmUsuarioTentaDesativarOutro() {
            String cns = "654987123065482";
            Usuario usuario = gerarUsuario(cns);
            Usuario usuarioLogado = gerarUsuario("123456789012345");

            when(repository.findByCns(anyString())).thenReturn(Optional.of(usuario));
            when(securityService.obterUsuarioLogado()).thenReturn(usuarioLogado);

            assertThatThrownBy(() -> service.desativar(cns))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoIguaisException.class)
                    .hasMessage(NAO_FOI_POSSIVEL_DESATIVAR_USUARIO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(repository, never()).save(any(Usuario.class));
        }
    }

    @Nested
    class AtivarUsuario {
        @Test
        void deveAtivarUsuario() {
            String cns = "654987123065482";
            Usuario usuario = gerarUsuario();

            when(repository.findByCns(anyString())).thenReturn(Optional.of(usuario));
            when(securityService.obterUsuarioLogado()).thenReturn(usuario);
            when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

            Usuario response = service.ativar(cns);

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Usuario.class)
                    .extracting(Usuario::getCns, Usuario::getAtivo)
                    .containsExactly(cns, true);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(repository, times(1)).save(any(Usuario.class));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarUsuario_CnsNaoEncontrado() {
            String cns = "654987123065482";

            when(repository.findByCns(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.ativar(cns))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoEncontradoException.class)
                    .hasMessage(NAO_FOI_POSSIVEL_ATIVAR_USUARIO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, never()).obterUsuarioLogado();
            verify(repository, never()).save(any(Usuario.class));
        }

        @Test
        void deveGerarExcecao_QUandoAtivarUsuario_UmUsuarioTentaDesativarOutro() {
            String cns = "654987123065482";
            Usuario usuario = gerarUsuario(cns);
            Usuario usuarioLogado = gerarUsuario("123456789012345");

            when(repository.findByCns(anyString())).thenReturn(Optional.of(usuario));
            when(securityService.obterUsuarioLogado()).thenReturn(usuarioLogado);

            assertThatThrownBy(() -> service.ativar(cns))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoIguaisException.class)
                    .hasMessage(NAO_FOI_POSSIVEL_ATIVAR_USUARIO);

            verify(repository, times(1)).findByCns(anyString());
            verify(securityService, times(1)).obterUsuarioLogado();
            verify(repository, never()).save(any(Usuario.class));
        }
    }
}
