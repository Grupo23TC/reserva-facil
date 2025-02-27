package br.com.fiap.hackathon.reservafacil.repository;

import br.com.fiap.hackathon.reservafacil.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface ReservaRepository extends JpaRepository<Reserva, UUID> {

    @Query("SELECT r FROM Reserva r WHERE r.prestador.id = :prestadorId AND r.dataReserva = :data")
    List<Reserva> findByPrestadorAndDataReserva(UUID prestadorId, LocalDateTime data);

    @Query("SELECT r.dataReserva FROM Reserva r WHERE r.prestador.id = :prestadorId AND r.dataReserva BETWEEN :dataInicio AND :dataFim " +
            "GROUP BY r.dataReserva HAVING COUNT(r) >= 3")
    List<LocalDateTime> findByPrestadorIdAndNaoDisponivelPeriodo(UUID prestadorId, LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query("SELECT r from Reserva r WHERE r.beneficiario.cns = :cns")
    List<Reserva> findAllByBeneficiario(String cns);

    List<Reserva> findAllByPrestadorId(UUID prestadorId);

}
