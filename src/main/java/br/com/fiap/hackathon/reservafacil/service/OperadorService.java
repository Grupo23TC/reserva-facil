package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.Operador;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.operador.CadastrarOperadorRequest;

public interface OperadorService {
    Operador cadastrar(CadastrarOperadorRequest request);

    Operador buscarPorCns(String cns);

    Operador ativar(String cns);

    Operador desativar(String cns);
}
