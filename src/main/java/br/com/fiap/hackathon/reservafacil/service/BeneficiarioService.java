package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;

public interface BeneficiarioService {
    Beneficiario cadastrar(CadastrarBeneficiarioRequest request);

    Beneficiario buscarPorCns(String cns);

    Beneficiario ativar(String cns);

    Beneficiario desativar(String cns);
}
