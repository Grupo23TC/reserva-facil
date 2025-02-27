package br.com.fiap.hackathon.reservafacil.mapper;

import br.com.fiap.hackathon.reservafacil.model.Prestador;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.PrestadorResponseDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PrestadorMapper {

    public static PrestadorResponseDTO toPrestadorResponseDTO(Prestador prestador){
        return new PrestadorResponseDTO(
                prestador.getId(),
                prestador.getNomeFantasia(),
                prestador.getEndereco(),
                prestador.getTipoPrestadorEnum()
//                prestador.getMedicamentos().stream().map(Medicamento::getNome).toList()
        );
    }

    public static List<PrestadorResponseDTO> toPrestadorResponseDTOList(List<Prestador> prestadores) {
        if (prestadores == null) {
            return Collections.emptyList();
        }
        return prestadores.stream()
                .map(PrestadorMapper::toPrestadorResponseDTO)
                .collect(Collectors.toList());
    }
}
