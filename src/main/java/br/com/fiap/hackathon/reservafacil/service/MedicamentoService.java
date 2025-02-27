package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.model.Medicamento;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.AtualizarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.CadastrarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.MedicamentoResponseDTO;

import java.util.List;
import java.util.UUID;

public interface MedicamentoService {

    MedicamentoResponseDTO cadastrarMedicamento(CadastrarMedicamentoRequestDTO dto);
    MedicamentoResponseDTO atualizarMedicamento(UUID id, AtualizarMedicamentoRequestDTO dto);
    void excluirMedicamento(UUID id);

    Medicamento buscarMedicamento(UUID id);
    MedicamentoResponseDTO buscarMedicamentoDTO(UUID id);
    List<MedicamentoResponseDTO> listarMedicamentos();
}
