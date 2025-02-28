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
    @Query("SELECT p FROM Prestador p WHERE LOWER(p.endereco.cidade) LIKE LOWER(:cidade)")
    List<Prestador> findByCidade(@Param("cidade") String cidade);

    @Query("SELECT p FROM Prestador p JOIN p.medicamentos m WHERE LOWER(m.nome) LIKE LOWER(:nomeMedicamento) AND m.quantidade > 0")
    List<Prestador> findByNomeMedicamento(@Param("nomeMedicamento") String nomeMedicamento);

    @Query("SELECT p FROM Prestador p JOIN p.medicamentos m WHERE LOWER(p.endereco.cidade) LIKE LOWER(:cidade) " +
            "AND LOWER(m.nome) LIKE LOWER(:nomeMedicamento) AND m.quantidade > 0")
    List<Prestador> findByCidadeAndNomeMedicamento(@Param("cidade") String cidade, @Param("nomeMedicamento") String nomeMedicamento);
}
