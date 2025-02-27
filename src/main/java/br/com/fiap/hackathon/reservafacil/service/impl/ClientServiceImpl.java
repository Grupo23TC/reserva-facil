package br.com.fiap.hackathon.reservafacil.service.impl;

import br.com.fiap.hackathon.reservafacil.model.Client;
import br.com.fiap.hackathon.reservafacil.repository.ClientRepository;
import br.com.fiap.hackathon.reservafacil.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repository;

    @Transactional
    public Client obterPorClientId(String clienteId) {
        return repository.findByClientId(clienteId);
    }
}
