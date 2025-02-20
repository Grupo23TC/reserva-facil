package br.com.fiap.hackathon.reservafacil.exception.beneficiario;

public class BeneficiarioNaoEncontradoException extends RuntimeException {
    public BeneficiarioNaoEncontradoException(String message) {
        super(message);
    }
}
