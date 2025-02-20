package br.com.fiap.hackathon.reservafacil.mapper;

import br.com.fiap.hackathon.reservafacil.model.Endereco;
import br.com.fiap.hackathon.reservafacil.model.dto.endereco.CadastrarEnderecoRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.endereco.EnderecoResponse;

public class EnderecoMapper {
    public static Endereco toEndereco(CadastrarEnderecoRequest request) {
        Endereco endereco = new Endereco();

        endereco.setLogradouro(request.logradouro());
        endereco.setBairro(request.bairro());
        endereco.setCidade(request.cidade());
        endereco.setEstado(request.estado());
        endereco.setComplemento(request.complemento());

        return endereco;
    }

    public static EnderecoResponse toEnderecoResponse(Endereco endereco) {
        return new EnderecoResponse(
                endereco.getLogradouro(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getComplemento()
        );
    }
}
