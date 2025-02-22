package br.com.fiap.hackathon.reservafacil.repository;

import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeneficiarioRepository extends JpaRepository<Beneficiario, UUID> {
    Optional<Beneficiario> findByCns(String cns);

    boolean existsByCns(String cns);

    boolean existsByCpf(String cpf);
}
