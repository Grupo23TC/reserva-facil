package br.com.fiap.hackathon.reservafacil.model.dto.beneficiario;

import br.com.fiap.hackathon.reservafacil.model.dto.usuario.CadastrarUsuarioRequest;
import br.com.fiap.hackathon.reservafacil.model.enums.FaixaEtariaEnum;
import br.com.fiap.hackathon.reservafacil.model.enums.GeneroEnum;
import br.com.fiap.hackathon.reservafacil.model.enums.TipoMedicamentoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

public record CadastrarBeneficiarioRequest(
        CadastrarUsuarioRequest usuario,

        @NotBlank(message = "Esse campo não pode estar vazio.")
        String nome,

        @NotBlank(message = "Esse campo não pode estar vazio.")
        @CPF(message = "CPF inválido")
        String cpf,

        @NotNull(message = "Esse campo não pode estar vazio.")
        FaixaEtariaEnum faixaEtaria,

        @NotNull(message = "Esse campo não pode estar vazio.")
        GeneroEnum genero,

        @NotNull(message = "Esse campo não pode estar vazio.")
        TipoMedicamentoEnum tipoMedicamento,

        @NotBlank(message = "Esse campo não pode estar vazio.")
        String endereco,

        @NotBlank(message = "Esse campo não pode estar vazio.")
        @Pattern(regexp = "^\\(?(\\d{2})?\\)?\\s?(9?\\d{4})[-.\\s]?(\\d{4})$", message = "O telefone deve conter um número válido.")
        String telefone
) {
}
