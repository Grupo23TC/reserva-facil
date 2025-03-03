package br.com.fiap.hackathon.reservafacil.util;

import br.com.fiap.hackathon.reservafacil.model.Documento;
import br.com.fiap.hackathon.reservafacil.model.Medicamento;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.AtualizarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.CadastrarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.MedicamentoResponseDTO;
import br.com.fiap.hackathon.reservafacil.model.enums.TipoMedicamentoEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MedicamentoUtil {

    public static CadastrarMedicamentoRequestDTO gerarCadastrarMedicamentoRequestDTO() {
        return new CadastrarMedicamentoRequestDTO(
                "Medicamento test",
                TipoMedicamentoEnum.ESPECIAIS,
                10,
                LocalDate.of(2025,05,15),
                "001",
                List.of("Doc 1", "Doc 2", "Doc 3")
        );
    }

    public static AtualizarMedicamentoRequestDTO gerarAtualizarMedicamentoRequestDTO() {
        return new AtualizarMedicamentoRequestDTO(
                "Nome alterado",
                10000,
                LocalDate.of(2025,05,10)
        );
    }

    public static Medicamento gerarMedicamento() {
        Medicamento medicamento = new Medicamento();
        medicamento.setId(UUID.randomUUID());
        medicamento.setNome("Medicamento Test");
        medicamento.setLote("001");
        medicamento.setPrestador(PrestadorUtil.gerarPrestador());
        medicamento.setValidade(LocalDate.of(2025,05,15));
        medicamento.setQuantidade(10);
        medicamento.setDocumentos(List.of(new Documento("Doc 1"), new Documento("Doc 2"), new Documento("Doc 3")));

        return medicamento;
    }

    public static Medicamento gerarMedicamento(UUID idPrestador) {
        Medicamento medicamento = new Medicamento();
        medicamento.setId(UUID.randomUUID());
        medicamento.setNome("Medicamento Test");
        medicamento.setLote("001");
        medicamento.setPrestador(PrestadorUtil.gerarPrestador(idPrestador));
        medicamento.setValidade(LocalDate.of(2025,05,15));
        medicamento.setQuantidade(10);
        medicamento.setDocumentos(List.of(new Documento("Doc 1"), new Documento("Doc 2"), new Documento("Doc 3")));

        return medicamento;
    }

    public static MedicamentoResponseDTO gerarMedicamentoResponseDTO() {
        return new MedicamentoResponseDTO(
                UUID.randomUUID(),
                "Medicamento test",
                TipoMedicamentoEnum.ESPECIAIS,
                10,
                LocalDate.of(2025,05,15).toString(),
                "001",
                "Prestador Test",
                List.of("Doc 1", "Doc 2", "Doc 3")

        );
    }
}
