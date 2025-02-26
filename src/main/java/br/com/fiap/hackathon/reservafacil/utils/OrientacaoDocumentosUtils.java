package br.com.fiap.hackathon.reservafacil.utils;

import br.com.fiap.hackathon.reservafacil.model.Medicamento;

public class OrientacaoDocumentosUtils {

    public static String orientacaoDocumentos(Medicamento medicamento) {
        if (medicamento.getDocumentos() == null || medicamento.getDocumentos().isEmpty()) {
            return null;
        }

        String orientacao = "Levar no dia da retirada do medicamento: " + medicamento.getNome() + " os seguintes documentos: ";

        for (int i = 0; i < medicamento.getDocumentos().size(); i++) {

            orientacao = orientacao.concat(medicamento.getDocumentos().get(i).getNome());
            if (i < medicamento.getDocumentos().size() - 1) {
                orientacao = orientacao.concat(", ");
            }

        }

        orientacao = orientacao.concat(".");

        return orientacao;
    }
}
