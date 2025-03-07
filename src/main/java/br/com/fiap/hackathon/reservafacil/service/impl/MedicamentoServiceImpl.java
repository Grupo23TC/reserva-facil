package br.com.fiap.hackathon.reservafacil.service.impl;

import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.AcessoNegadoException;
import br.com.fiap.hackathon.reservafacil.mapper.MedicamentoMapper;
import br.com.fiap.hackathon.reservafacil.model.Medicamento;
import br.com.fiap.hackathon.reservafacil.model.Operador;
import br.com.fiap.hackathon.reservafacil.model.Prestador;
import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.AtualizarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.CadastrarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.MedicamentoResponseDTO;
import br.com.fiap.hackathon.reservafacil.repository.MedicamentoRepository;
import br.com.fiap.hackathon.reservafacil.repository.OperadorRepository;
import br.com.fiap.hackathon.reservafacil.security.SecurityService;
import br.com.fiap.hackathon.reservafacil.service.MedicamentoService;
import br.com.fiap.hackathon.reservafacil.service.OperadorService;
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
    private SecurityService securityService;

    @Autowired
    private OperadorService operadorService;

    @Override
    @Transactional
    public MedicamentoResponseDTO cadastrarMedicamento(CadastrarMedicamentoRequestDTO dto) {
        Usuario usuarioLogado = securityService.obterUsuarioLogado();
        Operador operador = operadorService.buscarPorCns(usuarioLogado.getCns());
        Prestador prestador = operador.getPrestador();


        Medicamento medicamento = MedicamentoMapper.toMedicamento(dto);
        medicamento.setPrestador(prestador);
        return MedicamentoMapper.toMedicamentoResponseDTO(medicamentoRepository.save(medicamento));
    }

    @Override
    @Transactional
    public MedicamentoResponseDTO atualizarMedicamento(
            UUID id,
            AtualizarMedicamentoRequestDTO dto,
            boolean atualizandoPorReserva
    ) {
        Medicamento medicamento = buscarMedicamento(id);
        verificarOperadorEPrestador(
                medicamento.getPrestador().getId(),
                "Você não pode atualizar um medicamento de um prestador que você não está associado.",
                atualizandoPorReserva
        );

        if (dto.nome() != null && !dto.nome().isEmpty()) {
            medicamento.setNome(dto.nome());
        }
        if (dto.quantidade() != null) {
            medicamento.setQuantidade(dto.quantidade());
        }
        if (dto.validade() != null) {
            medicamento.setValidade(dto.validade());
        }
        return MedicamentoMapper.toMedicamentoResponseDTO(medicamentoRepository.save(medicamento));
    }

    @Override
    @Transactional
    public void excluirMedicamento(UUID id) {
        Medicamento medicamento = buscarMedicamento(id);
        verificarOperadorEPrestador(
                medicamento.getPrestador().getId(),
                "Você não pode deletar um medicamento de um prestador que você não está associado.",
                false
        );

        medicamentoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MedicamentoResponseDTO buscarMedicamentoDTO(UUID id) {
        return MedicamentoMapper.toMedicamentoResponseDTO(buscarMedicamento(id));
    }

    @Override
    @Transactional
    public Medicamento buscarMedicamento(UUID id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new MedicamentoNaoEncontradoException("Medicamento id: " + id + " não encontrado."));
    }

    @Override
    @Transactional
    public List<MedicamentoResponseDTO> listarMedicamentos() {
        return medicamentoRepository.findAll().stream().map(MedicamentoMapper::toMedicamentoResponseDTO).toList();
    }

    private void verificarOperadorEPrestador(UUID prestadorId, String message, boolean atualizandoPorReserva) {
        if (atualizandoPorReserva) return;
        Usuario usuarioLogado = securityService.obterUsuarioLogado();

        Operador operador = operadorService.buscarPorCns(usuarioLogado.getCns());

        if (!operador.getPrestador().getId().equals(prestadorId)) {
            throw new AcessoNegadoException(message);
        }
    }
}
