package br.com.fiap.hackathon.reservafacil.repository;

import br.com.fiap.hackathon.reservafacil.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicamentoRepository extends JpaRepository<Medicamento, UUID> {
    Optional<Medicamento> findById(UUID uuid);

    List<Medicamento> findByNome(String nome);

    @Query("SELECT m FROM Medicamento m JOIN m.prestador p WHERE p.id = :prestadorId")
    List<Medicamento> findByPrestadorId(@Param("prestadorId") UUID prestadorId);
}
