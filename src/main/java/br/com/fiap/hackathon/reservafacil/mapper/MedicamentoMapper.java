package br.com.fiap.hackathon.reservafacil.mapper;

import br.com.fiap.hackathon.reservafacil.model.Documento;
import br.com.fiap.hackathon.reservafacil.model.Medicamento;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.CadastrarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.MedicamentoResponseDTO;

import java.util.List;

public class MedicamentoMapper {

    public static Medicamento toMedicamento(CadastrarMedicamentoRequestDTO dto) {
        Medicamento medicamento = new Medicamento();
        medicamento.setTipo(dto.tipo());
        medicamento.setNome(dto.nome());
        medicamento.setQuantidade(dto.quantidade());
        medicamento.setValidade(dto.validade());
        medicamento.setLote(dto.lote());

        List<Documento> documentos = dto.documentos().stream().map(Documento::new).toList();

        medicamento.setDocumentos(documentos);

        return medicamento;
    }

    public static MedicamentoResponseDTO toMedicamentoResponseDTO(Medicamento medicamento) {
        return new MedicamentoResponseDTO(
                medicamento.getId(),
                medicamento.getNome(),
                medicamento.getTipo(),
                medicamento.getQuantidade(),
                medicamento.getValidade(),
                medicamento.getLote(),
                medicamento.getPrestador().getNome(),
                medicamento.getDocumentos().stream().map(Documento::getNome).toList()
        );
    }
}
