package br.com.fiap.hackathon.reservafacil.exception.medicamento;

public class MedicamentoNaoPertencePrestadorException extends RuntimeException {
    public MedicamentoNaoPertencePrestadorException(String message) {
        super(message);
    }
}
