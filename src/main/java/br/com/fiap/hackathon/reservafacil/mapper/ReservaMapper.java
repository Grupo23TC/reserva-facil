package br.com.fiap.hackathon.reservafacil.mapper;

import br.com.fiap.hackathon.reservafacil.model.Reserva;
import br.com.fiap.hackathon.reservafacil.model.dto.reserva.ReservaResponseDTO;
import br.com.fiap.hackathon.reservafacil.utils.OrientacaoDocumentosUtils;


public class ReservaMapper {

    public static ReservaResponseDTO toReservaResponseDTO(Reserva reserva) {
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getDataReserva(),
                reserva.getBeneficiario().getNome(),
                reserva.getPrestador().getNome(),
                reserva.getMedicamento().getNome(),
                OrientacaoDocumentosUtils.orientacaoDocumentos(reserva.getMedicamento())
        );
    }
}
