package br.com.fiap.hackathon.reservafacil.service.integracao;

import br.com.fiap.hackathon.reservafacil.Util.AuthUtil;
import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioNaoIguaisException;
import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.service.BeneficiarioService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static br.com.fiap.hackathon.reservafacil.Util.BeneficiarioUtil.gerarCadastrarBeneficiarioRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class BeneficiarioServiceIT {
    private static final String BENEFICIARIO_JA_EXISTE = "Beneficiário já cadastrado";
    private static final String ACESSO_NEGADO = "Você não pode ter acesso ou alterar os dados de outros beneficiários";
    private static final String BENEFICIARIO_NAO_ENCONTRADO = "Beneficiário não encontrado";

    @Autowired
    private BeneficiarioService service;

    @Nested
    class CadastraBeneficiario {
        @Test
        void deveCadastrarBeneficiario() {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest();

            Beneficiario beneficiario = service.cadastrar(request);

            assertThat(beneficiario)
                    .isNotNull()
                    .isInstanceOf(Beneficiario.class)
                    .extracting(Beneficiario::getCns, Beneficiario::getCpf)
                    .containsExactly(request.usuario().cns(), request.cpf());
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarBeneficiario_CnsJaCadastrado() {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest("123456789012345");

            assertThatThrownBy(() -> service.cadastrar(request))
                    .isNotNull()
                    .isInstanceOf(BeneficiarioCadastradoException.class)
                    .hasMessage(BENEFICIARIO_JA_EXISTE);
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarBeneficiario_CpfJaCadastrado() {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest("123456789012346", "01234567890");

            assertThatThrownBy(() -> service.cadastrar(request))
                    .isNotNull()
                    .isInstanceOf(BeneficiarioCadastradoException.class)
                    .hasMessage(BENEFICIARIO_JA_EXISTE);
        }
    }

    @Nested
    class BuscaBeneficiario {
        @Test
        void deveBuscarBeneficiario() {
            AuthUtil.autenticar("123456789012345");

            Beneficiario beneficiario = service.buscarPorCns("123456789012345");

            assertThat(beneficiario)
                    .isNotNull()
                    .isInstanceOf(Beneficiario.class);

            assertThat(beneficiario.getCns())
                    .isEqualTo(beneficiario.getCns());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPorBeneficiario_CnsNaoEncontrado() {
            AuthUtil.autenticar("123456789012345");

            assertThatThrownBy(() -> service.buscarPorCns("654987123065488"))
                    .isNotNull()
                    .isInstanceOf(BeneficiarioNaoEncontradoException.class)
                    .hasMessage(BENEFICIARIO_NAO_ENCONTRADO);
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPorCns_AcessoNegado() {
            AuthUtil.autenticar("123456789012345");

            assertThatThrownBy(() -> service.buscarPorCns("987654321098765"))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoIguaisException.class)
                    .hasMessage(ACESSO_NEGADO);
        }
    }

    @Nested
    class AtivarBeneficiario {
        @Test
        void deveAtivarBeneficiario() {
            AuthUtil.autenticar("987654321098765");

            Beneficiario response = service.ativar("987654321098765");

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Beneficiario.class)
                    .extracting(Beneficiario::getCns, Beneficiario::getAtivo)
                    .containsExactly("987654321098765", true);
        }

        @Test
        void deveGerarExcecao_QuandoAtivarBeneficiario_CnsNaoEncontrado() {
            AuthUtil.autenticar("987654321098765");

            assertThatThrownBy(() -> service.ativar("987654321098766"))
                    .isNotNull()
                    .isInstanceOf(BeneficiarioNaoEncontradoException.class)
                    .hasMessage(BENEFICIARIO_NAO_ENCONTRADO);
        }

        @Test
        void deveGerarExcecao_QuandoAtivarBeneficiario_AcessoNegado() {
            AuthUtil.autenticar("123456789012345");

            assertThatThrownBy(() -> service.ativar("987654321098765"))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoIguaisException.class)
                    .hasMessage(ACESSO_NEGADO);
        }
    }

    @Nested
    class DesativarBeneficiario {
        @Test
        void deveDesativarBeneficiario() {
            AuthUtil.autenticar("987654321098765");

            Beneficiario response = service.desativar("987654321098765");

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Beneficiario.class)
                    .extracting(Beneficiario::getCns, Beneficiario::getAtivo)
                    .containsExactly("987654321098765", false);
        }

        @Test
        void deveGerarExcecao_QuandoDesativarBeneficiario_CnsNaoEncontrado() {
            AuthUtil.autenticar("987654321098765");

            assertThatThrownBy(() -> service.desativar("987654321098766"))
                    .isNotNull()
                    .isInstanceOf(BeneficiarioNaoEncontradoException.class)
                    .hasMessage(BENEFICIARIO_NAO_ENCONTRADO);
        }

        @Test
        void deveGerarExcecao_QuandoDesativarBeneficiario_AcessoNegado() {
            AuthUtil.autenticar("123456789012345");

            assertThatThrownBy(() -> service.desativar("987654321098765"))
                    .isNotNull()
                    .isInstanceOf(UsuarioNaoIguaisException.class)
                    .hasMessage(ACESSO_NEGADO);
        }
    }
}
