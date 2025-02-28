package br.com.fiap.hackathon.reservafacil.exception.medicamento;

public class MedicamentoEsgotadoException extends RuntimeException {
    public MedicamentoEsgotadoException(String message) {
        super(message);
    }
}
