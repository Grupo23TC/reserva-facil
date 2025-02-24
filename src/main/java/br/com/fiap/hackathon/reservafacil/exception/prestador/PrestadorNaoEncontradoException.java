package br.com.fiap.hackathon.reservafacil.exception.prestador;

public class PrestadorNaoEncontradoException extends RuntimeException {
    public PrestadorNaoEncontradoException(String message) {
        super(message);
    }
}
