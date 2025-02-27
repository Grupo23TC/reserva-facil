package br.com.fiap.hackathon.reservafacil.service.integracao;

import br.com.fiap.hackathon.reservafacil.util.AuthUtil;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioNaoIguaisException;
import br.com.fiap.hackathon.reservafacil.model.Role;
import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.service.UsuarioService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UsuarioServiceIT {
    private static final String USUARIO_JA_CADASTRADO = "CNS já cadastrado.";
    private static final String ROLE_NAO_ENCONTRADA = "Não foi possível encontrar a role.";
    private static final String NAO_FOI_POSSIVEL_ALTERAR_A_SENHA = "Não foi possível alterar a senha.";
    private static final String NAO_FOI_POSSIVEL_DESATIVAR_USUARIO = "Não foi possível desativar usuário.";
    private static final String NAO_FOI_POSSIVEL_ATIVAR_USUARIO = "Não foi possível ativar usuário.";

    @Autowired
    private UsuarioService service;

    @Nested
    class CadastraUsuario {
        @Test
        void deveCadastrarUsuario() {
            String cns = "123456789012346";
            String senha = "123";
            String roleNome = "PACIENTE";

            Usuario response = service.salvarUsuario(cns, senha, roleNome);

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Usuario.class)
                    .extracting(Usuario::getCns, Usuario::getSenha)
                    .containsExactly(cns, response.getSenha());

            assertThat(
                    response
                            .getRoles()
                            .stream()
                            .map(Role::getAuthority)
                            .anyMatch(authority -> authority.equals(roleNome))
            )
                    .isTrue();
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarUsuario_CnsJaCadastrado() {
            String cns = "123456789012345";
            String senha = "123";
            String roleNome = "PACIENTE";

            assertThatThrownBy(() -> service.salvarUsuario(cns, senha, roleNome))
                    .isNotNull()
                    .isInstanceOf(UsuarioCadastradoException.class)
                    .hasMessageContaining(USUARIO_JA_CADASTRADO);
        }
    }

    @Nested
    class BuscarUsuario {
        @Test
        void deveOterUsuario() {
            String cns = "123456789012345";

            Optional<Usuario> usuario = service.obterPorCns(cns);

            assertThat(usuario.isPresent()).isTrue();

            assertThat(usuario.get().getCns())
                    .isEqualTo(cns);
        }

        @Test
        void deveObterUsuarioPorCns_UsuarioNaoEncontrado() {
            String cns = "123456789012347";

            Optional<Usuario> usuarioOptional = service.obterPorCns(cns);

            assertThat(usuarioOptional.isEmpty()).isTrue();
        }
    }

    @Nested
    class AtualizarUsuario {
        @Test
        void deveAtualizaSenhaUsuario() {
            AuthUtil.autenticar("987654321098765");
            String cns = "987654321098765";
            String novaSenha = "123456";

            service.atualizarSenhaUsuario(cns, novaSenha);
        }

        @Test
        void deveGerarExcecao_QuandoAtualizarSenhaUsuario_CnsNaoEncontrado() {
            AuthUtil.autenticar("987654321098765");
            String cns = "987654321098766";
            String novaSenha = "123456";

            assertThatThrownBy(() -> service.atualizarSenhaUsuario(cns, novaSenha))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoEncontradoException.class)
                    .hasMessage(NAO_FOI_POSSIVEL_ALTERAR_A_SENHA);
        }

        @Test
        void deveGerarExcecao_QuandoAtualizarSenhaUsuario_AcessoNegado() {
            AuthUtil.autenticar("123456789021345");
            String cns = "987654321098765";
            String novaSenha = "123456";

            assertThatThrownBy(() -> service.atualizarSenhaUsuario(cns, novaSenha))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoIguaisException.class)
                    .hasMessage(NAO_FOI_POSSIVEL_ALTERAR_A_SENHA);
        }
    }

    @Nested
    class DesativarUsuario {
        @Test
        void deveDesativarUsuario() {
            AuthUtil.autenticar("987654321098765");
            String cns = "987654321098765";

            Usuario response = service.desativar(cns);

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Usuario.class)
                    .extracting(Usuario::getCns, Usuario::getAtivo)
                    .containsExactly(cns, false);
        }

        @Test
        void deveGerarExcecao_QuandoDesativarUsuario_CnsNaoEncontrado() {
            AuthUtil.autenticar("987654321098765");
            String cns = "987654321098766";

            assertThatThrownBy(() -> service.desativar(cns))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoEncontradoException.class)
                    .hasMessage(NAO_FOI_POSSIVEL_DESATIVAR_USUARIO);
        }

        @Test
        void deveGerarExcecao_QUandoDesativarUsuario_UmUsuarioTentaDesativarOutro() {
            AuthUtil.autenticar("123456789012345");
            String cns = "987654321098765";

            assertThatThrownBy(() -> service.desativar(cns))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoIguaisException.class)
                    .hasMessage(NAO_FOI_POSSIVEL_DESATIVAR_USUARIO);
        }
    }

    @Nested
    class AtivarUsuario {
        @Test
        void deveAtivarUsuario() {
            AuthUtil.autenticar("987654321098765");
            String cns = "987654321098765";

            Usuario response = service.ativar(cns);

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Usuario.class)
                    .extracting(Usuario::getCns, Usuario::getAtivo)
                    .containsExactly(cns, true);
        }

        @Test
        void deveGerarExcecao_QuandoAtivarUsuario_CnsNaoEncontrado() {
            AuthUtil.autenticar("987654321098765");
            String cns = "987654321098766";

            assertThatThrownBy(() -> service.ativar(cns))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoEncontradoException.class)
                    .hasMessage(NAO_FOI_POSSIVEL_ATIVAR_USUARIO);
        }

        @Test
        void deveGerarExcecao_QUandoAtivarUsuario_AcessoNegado() {
            AuthUtil.autenticar("123456789012345");
            String cns = "987654321098765";

            assertThatThrownBy(() -> service.ativar(cns))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoIguaisException.class)
                    .hasMessage(NAO_FOI_POSSIVEL_ATIVAR_USUARIO);
        }
    }
}
