package br.com.fiap.hackathon.reservafacil.util;

import br.com.fiap.hackathon.reservafacil.model.Endereco;
import br.com.fiap.hackathon.reservafacil.model.dto.endereco.CadastrarEnderecoRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.endereco.EnderecoResponse;

import java.util.UUID;

public class EnderecoUtil {
    public static CadastrarEnderecoRequest gerarCadastrarEnderecoRequest() {
        return new CadastrarEnderecoRequest(
                "Rua XPTO",
                "Avenida indefinida",
                "São Paulo",
                "SP",
                ""
        );
    }

    public static Endereco gerarEndereco() {
        return new Endereco(
                UUID.randomUUID(),
                "Rua XPTO",
                "Avenida indefinida",
                "São Paulo",
                "SP",
                ""
        );
    }

    public static EnderecoResponse gerarEnderecoResponse() {
        return new EnderecoResponse(
                "Rua XPTO",
                "Avenida indefinida",
                "São Paulo",
                "SP",
                ""
        );
    }
}
