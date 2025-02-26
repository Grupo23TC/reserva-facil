package br.com.fiap.hackathon.reservafacil.exception.reserva;

public class DataReservaNaoDisponivelException extends RuntimeException {
    public DataReservaNaoDisponivelException(String message) {
        super(message);
    }
}
