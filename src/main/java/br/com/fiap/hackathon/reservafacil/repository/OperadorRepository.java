package br.com.fiap.hackathon.reservafacil.repository;

import br.com.fiap.hackathon.reservafacil.model.Operador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperadorRepository extends JpaRepository<Operador, String> {
    Optional<Operador> findByCns(String cns);

    boolean existsByCns(String cns);
}
