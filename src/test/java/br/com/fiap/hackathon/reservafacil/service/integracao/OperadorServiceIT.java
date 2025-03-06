package br.com.fiap.hackathon.reservafacil.service.integracao;

import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.operador.OperadorCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.operador.OperadorNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.AcessoNegadoException;
import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.Operador;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.operador.CadastrarOperadorRequest;
import br.com.fiap.hackathon.reservafacil.service.impl.BeneficiarioServiceImpl;
import br.com.fiap.hackathon.reservafacil.service.impl.OperadorServiceImpl;
import br.com.fiap.hackathon.reservafacil.util.AuthUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static br.com.fiap.hackathon.reservafacil.util.BeneficiarioUtil.gerarCadastrarBeneficiarioRequest;
import static br.com.fiap.hackathon.reservafacil.util.OperadorUtil.gerarCadastrarOperadorRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OperadorServiceIT {
    private static final String OPERADOR_JA_EXISTE = "Esse operador já foi cadastrado";
    private static final String ACESSO_NEGADO = "Você não pode ter acesso ou alterar os dados de outros operadores";
    private static final String OPERADOR_NAO_ENCONTRADO = "Operador não encontrado";

    @Autowired
    private OperadorServiceImpl service;

    @Nested
    class CadastraOperador {
        @Test
        void deveCadastrarOperador() {
            CadastrarOperadorRequest request = gerarCadastrarOperadorRequest(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));

            Operador operadorSalvo = service.cadastrar(request);

            assertThat(operadorSalvo)
                    .isNotNull()
                    .isInstanceOf(Operador.class)
                    .extracting(Operador::getCns, Operador::getCargo, Operador::getNome)
                    .containsExactly(operadorSalvo.getCns(), operadorSalvo.getCargo(), operadorSalvo.getNome());
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarOperador_CnsJaCadastrado() {
            CadastrarOperadorRequest request = gerarCadastrarOperadorRequest("234567890123456");

            assertThatThrownBy(() -> service.cadastrar(request))
                    .isNotNull()
                    .isInstanceOf(OperadorCadastradoException.class)
                    .hasMessage(OPERADOR_JA_EXISTE);
        }
    }

    @Nested
    class BuscaOperador {
        @Test
        void deveBuscarOperador() {
            AuthUtil.autenticar("234567890123456");

            Operador operador = service.buscarPorCns("234567890123456");

            assertThat(operador)
                    .isNotNull()
                    .isInstanceOf(Operador.class);

            assertThat(operador.getCns())
                    .isEqualTo("234567890123456");
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPorCns_CnsNaoEncontrado() {
            AuthUtil.autenticar("234567890123456");

            assertThatThrownBy(() -> service.buscarPorCns("654987123065498"))
                    .isNotNull()
                    .isInstanceOf(OperadorNaoEncontradoException.class)
                    .hasMessage(OPERADOR_NAO_ENCONTRADO);
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPorCns_AcessoNegado() {
            AuthUtil.autenticar("234567890123456");

            assertThatThrownBy(() -> service.buscarPorCns("876543210987654"))
                    .isNotNull()
                    .isInstanceOf(AcessoNegadoException.class)
                    .hasMessage(ACESSO_NEGADO);
        }
    }

    @Nested
    class AtivarOperador {
        @Test
        void deveAtivarOperador() {
            AuthUtil.autenticar("234567890123456");

            Operador response = service.ativar("234567890123456");

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Operador.class)
                    .extracting(Operador::getCns, Operador::getAtivo)
                    .containsExactly("234567890123456", true);
        }

        @Test
        void deveGerarExcecao_QuandoAtivarOperador_CnsNaoEncontrado() {
            AuthUtil.autenticar("234567890123456");

            assertThatThrownBy(() -> service.ativar("987654321098784"))
                    .isNotNull()
                    .isInstanceOf(OperadorNaoEncontradoException.class)
                    .hasMessage(OPERADOR_NAO_ENCONTRADO);
        }

        @Test
        void deveGerarExcecao_QuandoAtivarOperador_AcessoNegado() {
            AuthUtil.autenticar("234567890123456");

            assertThatThrownBy(() -> service.ativar("876543210987654"))
                    .isNotNull()
                    .isInstanceOf(AcessoNegadoException.class)
                    .hasMessage(ACESSO_NEGADO);
        }
    }

    @Nested
    class DesativarOperador {
        @Test
        void deveDesativarOperador() {
            AuthUtil.autenticar("234567890123456");

            Operador response = service.desativar("234567890123456");

            assertThat(response)
                    .isNotNull()
                    .isInstanceOf(Operador.class)
                    .extracting(Operador::getCns, Operador::getAtivo)
                    .containsExactly("234567890123456", false);
        }

        @Test
        void deveGerarExcecao_QuandoDesativarOperador_CnsNaoEncontrado() {
            AuthUtil.autenticar("234567890123456");

            assertThatThrownBy(() -> service.desativar("234567890123457"))
                    .isNotNull()
                    .isInstanceOf(OperadorNaoEncontradoException.class)
                    .hasMessage(OPERADOR_NAO_ENCONTRADO);
        }

        @Test
        void deveGerarExcecao_QuandoDesativarOperador_AcessoNegado() {
            AuthUtil.autenticar("234567890123456");

            assertThatThrownBy(() -> service.desativar("876543210987654"))
                    .isNotNull()
                    .isInstanceOf(AcessoNegadoException.class)
                    .hasMessage(ACESSO_NEGADO);
        }
    }
}
