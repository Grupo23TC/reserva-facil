package br.com.fiap.hackathon.reservafacil.exception.usuario;

public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String message) {
        super(message);
    }
}
