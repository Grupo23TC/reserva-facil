package br.com.fiap.hackathon.reservafacil.exception.reserva;

public class ReservaNaoEncontradaException extends RuntimeException {
    public ReservaNaoEncontradaException(String message) {
        super(message);
    }
}
