package br.com.fiap.hackathon.reservafacil.service.integracao;

import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.model.Role;
import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.service.UsuarioService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
}
