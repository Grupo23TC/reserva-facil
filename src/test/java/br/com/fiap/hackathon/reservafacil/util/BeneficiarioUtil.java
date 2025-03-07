package br.com.fiap.hackathon.reservafacil.util;

import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.BeneficiarioResponse;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.model.enums.FaixaEtariaEnum;
import br.com.fiap.hackathon.reservafacil.model.enums.GeneroEnum;
import br.com.fiap.hackathon.reservafacil.model.enums.TipoMedicamentoEnum;

import static br.com.fiap.hackathon.reservafacil.util.EnderecoUtil.*;
import static br.com.fiap.hackathon.reservafacil.util.UsuarioUtil.gerarCadastrarUsuarioRequest;
import static br.com.fiap.hackathon.reservafacil.util.UsuarioUtil.gerarUsuario;

public class BeneficiarioUtil {
    public static CadastrarBeneficiarioRequest gerarCadastrarBeneficiarioRequest() {
        return new CadastrarBeneficiarioRequest(
                gerarCadastrarUsuarioRequest(),
                "Lucas",
                "01234567891",
                FaixaEtariaEnum.valueOf(29),
                GeneroEnum.MASCULINO,
                TipoMedicamentoEnum.TARJA_PRETA,
                gerarCadastrarEnderecoRequest(),
                "00999999999"
        );
    }

    public static CadastrarBeneficiarioRequest gerarCadastrarBeneficiarioRequest(String cns) {
        return new CadastrarBeneficiarioRequest(
                gerarCadastrarUsuarioRequest(cns),
                "Lucas",
                "01234568790",
                FaixaEtariaEnum.valueOf(29),
                GeneroEnum.MASCULINO,
                TipoMedicamentoEnum.TARJA_PRETA,
                gerarCadastrarEnderecoRequest(),
                "00999999999"
        );
    }

    public static CadastrarBeneficiarioRequest gerarCadastrarBeneficiarioRequest(String cns, String cpf) {
        return new CadastrarBeneficiarioRequest(
                gerarCadastrarUsuarioRequest(cns),
                "Lucas",
                cpf,
                FaixaEtariaEnum.valueOf(29),
                GeneroEnum.MASCULINO,
                TipoMedicamentoEnum.TARJA_PRETA,
                gerarCadastrarEnderecoRequest(),
                "00999999999"
        );
    }

    public static Beneficiario gerarBeneficiario() {
        return new Beneficiario(
                "654987123065482",
                "Lucas",
                "01234567890",
                "00999999999",
                FaixaEtariaEnum.FAIXA_29_a_33,
                gerarEndereco(),
                GeneroEnum.MASCULINO,
                TipoMedicamentoEnum.TARJA_PRETA,
                true,
                gerarUsuario()
        );
    }

    public static Beneficiario gerarBeneficiario(boolean ativo) {
        return new Beneficiario(
                "654987123065482",
                "Lucas",
                "01234567890",
                "00999999999",
                FaixaEtariaEnum.FAIXA_29_a_33,
                gerarEndereco(),
                GeneroEnum.MASCULINO,
                TipoMedicamentoEnum.TARJA_PRETA,
                false,
                gerarUsuario()
        );
    }

    public static BeneficiarioResponse gerarBeneficiarioResponse() {
        return new BeneficiarioResponse(
                "654987123065482",
                "Lucas",
                "01234567890",
                "00999999999",
                FaixaEtariaEnum.FAIXA_29_a_33,
                gerarEnderecoResponse(),
                GeneroEnum.MASCULINO,
                TipoMedicamentoEnum.TARJA_PRETA,
                true
        );
    }

    public static BeneficiarioResponse gerarBeneficiarioResponse(boolean ativo) {
        return new BeneficiarioResponse(
                "654987123065482",
                "Lucas",
                "01234567890",
                "00999999999",
                FaixaEtariaEnum.FAIXA_29_a_33,
                gerarEnderecoResponse(),
                GeneroEnum.MASCULINO,
                TipoMedicamentoEnum.TARJA_PRETA,
                ativo
        );
    }
}
