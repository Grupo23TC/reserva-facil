package br.com.fiap.hackathon.reservafacil.mapper;

import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.Role;
import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.BeneficiarioResponse;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeneficiarioMapper {

    public BeneficiarioResponse toResponse(Beneficiario model) {
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
