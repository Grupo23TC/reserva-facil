package br.com.fiap.hackathon.reservafacil.exception.validacao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CampoErro {
    private final String campo;
    private final String mensagem;
}
