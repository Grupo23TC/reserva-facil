package br.com.fiap.hackathon.reservafacil.exception.validacao;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidacaoErro extends ErroCustomizado {
    private final List<CampoErro> campoErros = new ArrayList<CampoErro>();

    public ValidacaoErro(LocalDateTime horario, Integer status, String erro, String rota) {
        super(horario, status, erro, rota);
    }

    public void addErros(String campo, String mensagem) {
        campoErros.removeIf(erro -> erro.getCampo().equals(campo));
        campoErros.add(new CampoErro(campo, mensagem));
    }
}
