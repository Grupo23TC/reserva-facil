package br.com.fiap.hackathon.reservafacil.mapper;

import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.Operador;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.BeneficiarioResponse;
import br.com.fiap.hackathon.reservafacil.model.dto.operador.OperadorResponse;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.PrestadorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperadorMapper {

    public static OperadorResponse toResponse(Operador model) {
        PrestadorResponseDTO prestador = new PrestadorResponseDTO(
                model.getPrestador().getId(),
                model.getPrestador().getNomeFantasia(),
                model.getPrestador().getEndereco(),
                model.getPrestador().getTipoPrestadorEnum()
        );

        return new OperadorResponse(
                model.getCns(),
                model.getNome(),
                model.getCargo(),
                prestador,
                model.getAtivo()
        );
    }
}
