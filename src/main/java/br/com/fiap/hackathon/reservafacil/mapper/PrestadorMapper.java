package br.com.fiap.hackathon.reservafacil.mapper;

import br.com.fiap.hackathon.reservafacil.model.Prestador;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.PrestadorResponseDTO;

public class PrestadorMapper {

    public static PrestadorResponseDTO toPrestadorResponseDTO(Prestador prestador){
        return new PrestadorResponseDTO(
                prestador.getNome(),
                prestador.getNomeFantasia(),
                prestador.getTipoPrestadorEnum()
        );
    }
}
