package br.com.fiap.hackathon.reservafacil.repository;

import br.com.fiap.hackathon.reservafacil.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MedicamentoRepository extends JpaRepository<Medicamento, UUID> {
}
