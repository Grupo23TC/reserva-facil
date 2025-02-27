package br.com.fiap.hackathon.reservafacil.Util;

import br.com.fiap.hackathon.reservafacil.model.Endereco;
import br.com.fiap.hackathon.reservafacil.model.Prestador;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.CadastrarPrestadorRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.PrestadorResponseDTO;
import br.com.fiap.hackathon.reservafacil.model.enums.TipoPrestadorEnum;

import java.util.ArrayList;
import java.util.UUID;

public class PrestadorUtil {

    public static Prestador gerarPrestador(){
        Prestador prestador = new Prestador();
        prestador.setId(UUID.randomUUID());
        prestador.setNome("Hospital Nipo Brasileiro");
        prestador.setTipoPrestadorEnum(TipoPrestadorEnum.UBS);
        prestador.setEndereco(gerarEndereco());
        prestador.setNomeFantasia("UBS BR");
        prestador.setMedicamentos(new ArrayList<>());
        return prestador;
    }

    public static Endereco gerarEndereco(){
        Endereco endereco = new Endereco();
        endereco.setLogradouro("teste");
        return endereco;
    }

    public static PrestadorResponseDTO gerarPrestadorResponse(){
        return new PrestadorResponseDTO(
                UUID.randomUUID(),
                "Hospital Nipo Brasil",
                gerarEndereco(),
                TipoPrestadorEnum.UBS
        );
    }
    public static CadastrarPrestadorRequestDTO gerarCadastrarPrestadorRequestDTO() {
        return new CadastrarPrestadorRequestDTO(
                "Hospital UBS",
                "Hospital Nipo Brasil",
                gerarEndereco(),
                TipoPrestadorEnum.UBS
        );
    }
}
