package br.com.fiap.hackathon.reservafacil.mapper;

import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.BeneficiarioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeneficiarioMapper {

    public static BeneficiarioResponse toResponse(Beneficiario model) {
        return new BeneficiarioResponse(
                model.getCns(),
                model.getNome(),
                model.getCpf(),
                model.getTelefone(),
                model.getFaixaEtariaEnum(),
                EnderecoMapper.toEnderecoResponse(model.getEndereco()),
                model.getGenero(),
                model.getTipoMedicamento(),
                model.getAtivo()
        );
    }
}
