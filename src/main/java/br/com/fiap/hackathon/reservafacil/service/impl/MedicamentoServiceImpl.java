package br.com.fiap.hackathon.reservafacil.service.impl;

import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.mapper.MedicamentoMapper;
import br.com.fiap.hackathon.reservafacil.model.Medicamento;
import br.com.fiap.hackathon.reservafacil.model.Prestador;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.AtualizarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.CadastrarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.MedicamentoResponseDTO;
import br.com.fiap.hackathon.reservafacil.repository.MedicamentoRepository;
import br.com.fiap.hackathon.reservafacil.service.MedicamentoService;
import br.com.fiap.hackathon.reservafacil.service.PrestadorService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MedicamentoServiceImpl implements MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private PrestadorService prestadorService;

    @Override
    @Transactional
    public MedicamentoResponseDTO cadastrarMedicamento(CadastrarMedicamentoRequestDTO dto) {
        Prestador prestador = prestadorService.buscarPrestadorPorId(dto.prestadorId());
        Medicamento medicamento = MedicamentoMapper.toMedicamento(dto);
        medicamento.setPrestador(prestador);
        return MedicamentoMapper.toMedicamentoResponseDTO(medicamentoRepository.save(medicamento));
    }

    @Override
    @Transactional
    public MedicamentoResponseDTO atualizarMedicamento(UUID id, AtualizarMedicamentoRequestDTO dto) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new MedicamentoNaoEncontradoException("Medicamento id: " + id + " não encontrado."));
        medicamento.setNome(dto.nome());
        medicamento.setQuantidade(dto.quantidade());
        medicamento.setValidade(dto.validade());
        return MedicamentoMapper.toMedicamentoResponseDTO(medicamentoRepository.save(medicamento));
    }

    @Override
    @Transactional
    public void excluirMedicamento(UUID id) {
        buscarMedicamento(id);
        medicamentoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MedicamentoResponseDTO buscarMedicamento(UUID id) {
        return MedicamentoMapper.toMedicamentoResponseDTO(medicamentoRepository.findById(id)
                .orElseThrow(() -> new MedicamentoNaoEncontradoException("Medicamento id: " + id + " não encontrado.")));
    }

    @Override
    @Transactional
    public List<MedicamentoResponseDTO> listarMedicamentos() {
        return medicamentoRepository.findAll().stream().map(MedicamentoMapper::toMedicamentoResponseDTO).toList();
    }
}
