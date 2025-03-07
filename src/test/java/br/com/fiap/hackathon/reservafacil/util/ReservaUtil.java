package br.com.fiap.hackathon.reservafacil.util;

import br.com.fiap.hackathon.reservafacil.model.Reserva;
import br.com.fiap.hackathon.reservafacil.model.dto.reserva.CadastrarReservaRequestDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReservaUtil {

    public static CadastrarReservaRequestDTO gerarCadastrarReservaRequestDTO() {
        return new CadastrarReservaRequestDTO(
                LocalDateTime.now().plusDays(1).withMinute(0),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    public static CadastrarReservaRequestDTO gerarCadastrarReservaRequestDTO_DataPassada() {
        return new CadastrarReservaRequestDTO(
                LocalDateTime.now().minusDays(3).withMinute(0),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    public static CadastrarReservaRequestDTO gerarCadastrarReservaRequestDTO_DataInvalida() {
        return new CadastrarReservaRequestDTO(
                LocalDateTime.now().plusDays(1).withMinute(7),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    public static Reserva gerarReserva() {
        Reserva reserva = new Reserva();
        reserva.setId(UUID.randomUUID());
        reserva.setDataReserva(LocalDateTime.now().plusDays(1).withMinute(0));
        reserva.setBeneficiario(BeneficiarioUtil.gerarBeneficiario());
        reserva.setPrestador(PrestadorUtil.gerarPrestador());
        reserva.setMedicamento(MedicamentoUtil.gerarMedicamento());
        return reserva;
    }
}
