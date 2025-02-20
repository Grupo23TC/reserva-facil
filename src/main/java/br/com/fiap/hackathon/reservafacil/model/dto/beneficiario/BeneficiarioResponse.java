package br.com.fiap.hackathon.reservafacil.model.dto.beneficiario;

import br.com.fiap.hackathon.reservafacil.model.dto.endereco.EnderecoResponse;
import br.com.fiap.hackathon.reservafacil.model.enums.FaixaEtariaEnum;
import br.com.fiap.hackathon.reservafacil.model.enums.GeneroEnum;
import br.com.fiap.hackathon.reservafacil.model.enums.TipoMedicamentoEnum;

public record BeneficiarioResponse(
     String cns,
     String nome,
     String cpf,
     String telefone,
     FaixaEtariaEnum faixaEtariaEnum,
     EnderecoResponse endereco,
     GeneroEnum genero,
     TipoMedicamentoEnum tipoMedicamento,
     Boolean ativo
) {
}
