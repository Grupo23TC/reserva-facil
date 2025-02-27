package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.model.Client;

public interface ClientService {
    Client obterPorClientId(String clienteId);
}
