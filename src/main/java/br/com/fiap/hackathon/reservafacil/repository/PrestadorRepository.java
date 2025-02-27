package br.com.fiap.hackathon.reservafacil.repository;

import br.com.fiap.hackathon.reservafacil.model.Prestador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrestadorRepository extends JpaRepository<Prestador, UUID> {
    @Query("SELECT p FROM Prestador p WHERE p.endereco.logradouro = :logradouro")
    List<Prestador> findByLogradouro(@Param("logradouro") String logradouro);

    @Query("SELECT p FROM Prestador p JOIN p.medicamentos m WHERE m.nome = :nomeMedicamento")
    List<Prestador> findByNomeMedicamento(@Param("nomeMedicamento") String nomeMedicamento);

    @Query("SELECT p FROM Prestador p JOIN p.medicamentos m WHERE p.endereco.logradouro = :logradouro AND m.nome = :nomeMedicamento")
    List<Prestador> findByLocalidadeAndNomeMedicamento(@Param("logradouro") String logradouro, @Param("nomeMedicamento") String nomeMedicamento);
}
