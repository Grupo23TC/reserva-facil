package br.com.fiap.hackathon.reservafacil.exception.medicamento;

public class MedicamentoNaoEncontradoException extends RuntimeException {
    public MedicamentoNaoEncontradoException(String message) {
        super(message);
    }
}
