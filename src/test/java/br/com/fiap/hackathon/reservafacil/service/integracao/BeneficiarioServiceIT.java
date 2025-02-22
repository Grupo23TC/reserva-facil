package br.com.fiap.hackathon.reservafacil.service.integracao;

import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.service.BeneficiarioService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static br.com.fiap.hackathon.reservafacil.Util.BeneficiarioUtil.gerarCadastrarBeneficiarioRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class BeneficiarioServiceIT {
    private static final String BENEFICIARIO_JA_EXISTE = "Beneficiário já cadastrado";

    @Autowired
    private BeneficiarioService service;

    @Nested
    class CadastrarBeneficiario {
        @Test
        void deveCadastrarBeneficiario() {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest("987654321098745", "01234567890");

            Beneficiario beneficiario = service.cadastrar(request);

            assertThat(beneficiario)
                    .isNotNull()
                    .isInstanceOf(Beneficiario.class)
                    .extracting(Beneficiario::getCns, Beneficiario::getCpf, Beneficiario::getNome)
                    .containsExactly(request.usuario().cns(), request.cpf(), request.nome());
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarBeneficiario_CnsJaCadastrado() {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest("123456789012345", "01234567890");

            System.out.println(request);

            service.buscarPorCns("123456789012345");

            assertThatThrownBy(() -> service.cadastrar(request))
                    .isNotNull()
                    .isInstanceOf(BeneficiarioCadastradoException.class)
                    .hasMessage(BENEFICIARIO_JA_EXISTE);
        }
    }
}
