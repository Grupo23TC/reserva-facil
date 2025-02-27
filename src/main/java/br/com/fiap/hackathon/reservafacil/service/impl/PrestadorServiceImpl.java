package br.com.fiap.hackathon.reservafacil.service.impl;

import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.prestador.PrestadorNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.mapper.PrestadorMapper;
import br.com.fiap.hackathon.reservafacil.model.Medicamento;
import br.com.fiap.hackathon.reservafacil.model.Prestador;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.AtualizarPrestadorRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.CadastrarPrestadorRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.PrestadorResponseDTO;
import br.com.fiap.hackathon.reservafacil.repository.MedicamentoRepository;
import br.com.fiap.hackathon.reservafacil.repository.PrestadorRepository;
import br.com.fiap.hackathon.reservafacil.service.PrestadorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrestadorServiceImpl implements PrestadorService {
    public final PrestadorRepository prestadorRepository;

    public final MedicamentoRepository medicamentoRepository;

    @Override
    @Transactional
    public Page<PrestadorResponseDTO> listarPrestadores(Pageable pageable) {
        return prestadorRepository.findAll(pageable).map(PrestadorMapper::toPrestadorResponseDTO);
    }
    @Override
    @Transactional
    public PrestadorResponseDTO cadastrarPrestador(CadastrarPrestadorRequestDTO prestadorRequestDTO) {
        Prestador prestador = new Prestador();
        prestador.setNome(prestadorRequestDTO.nome());
        prestador.setNomeFantasia(prestadorRequestDTO.nomeFantasia());
        prestador.setEndereco(prestadorRequestDTO.endereco());
        prestador.setTipoPrestadorEnum(prestadorRequestDTO.tipoPrestador());

        return PrestadorMapper.toPrestadorResponseDTO(prestadorRepository.save(prestador));
    }

    @Override
    @Transactional
    public PrestadorResponseDTO atualizarPrestador(UUID id, AtualizarPrestadorRequestDTO prestadorRequestDTO) {
        Prestador prestador = buscarPrestadorPorId(id);
        prestador.setNome(prestadorRequestDTO.nome());
        prestador.setNomeFantasia(prestadorRequestDTO.nomeFantasia());
        prestador.setEndereco(prestadorRequestDTO.endereco());
        prestador.setTipoPrestadorEnum(prestadorRequestDTO.tipoPrestador());

        return PrestadorMapper.toPrestadorResponseDTO(prestadorRepository.save(prestador));
    }

    @Override
    @Transactional
    public void excluirPrestador(UUID id) {
        buscarPrestadorPorId(id);
        prestadorRepository.deleteById(id);
    }
    @Override
    @Transactional
    public Prestador buscarPrestadorPorId(UUID id) {
        return prestadorRepository.findById(id).orElseThrow(() -> new PrestadorNaoEncontradoException("Prestador de id: " + id + " não encontrado."));
    }
    @Override
    @Transactional
    public List<PrestadorResponseDTO> buscarPrestadorPorLocalidade(String localidade) {
        return prestadorRepository.findByLogradouro(localidade).stream().map(PrestadorMapper::toPrestadorResponseDTO).toList();
    }
    @Override
    @Transactional
    public List<PrestadorResponseDTO> buscarPrestadoresPorMedicamentoELocalidade(String localidade, String nomeMedicamento) {
        List<Prestador> prestadores = prestadorRepository.findByLocalidadeAndNomeMedicamento(localidade, nomeMedicamento);

        List<Prestador> prestadoresFiltrados = filtrarPrestadoresDisponiveis(prestadores, nomeMedicamento);

        return PrestadorMapper.toPrestadorResponseDTOList(prestadoresFiltrados);
    }

    @Override
    @Transactional
    public List<PrestadorResponseDTO> buscarPrestadoresPorMedicamentoDisponivel(String nomeMedicamento) {
        List<Prestador> prestadores = prestadorRepository.findByNomeMedicamento(nomeMedicamento);

        List<Prestador> prestadoresFiltrados = filtrarPrestadoresDisponiveis(prestadores, nomeMedicamento);

        return PrestadorMapper.toPrestadorResponseDTOList(prestadoresFiltrados);
    }

    private List<Prestador> filtrarPrestadoresDisponiveis(List<Prestador> prestadores, String nomeMedicamento) {
        return prestadores.stream()
                .filter(prestador -> verificaDisponibilidadeDoMedicamento(nomeMedicamento, prestador))
                .collect(Collectors.toList());
    }

    private boolean verificaDisponibilidadeDoMedicamento(String nomeMedicamento, Prestador prestador) {
        List<Medicamento> medicamentos = medicamentoRepository.findByNome(nomeMedicamento);
        if (medicamentos.isEmpty()) {
            throw new MedicamentoNaoEncontradoException("Nenhum(a): " + nomeMedicamento + " encontrado(a).");
        }

        List<Medicamento> medicamentosPrestador = medicamentoRepository.findByPrestadorId(prestador.getId());

        for (Medicamento med : medicamentosPrestador) {
            for(Medicamento medicamento: medicamentos){
                if (med.getNome().equals(medicamento.getNome()) && med.getQuantidade() == 0) {
                    throw new PrestadorNaoEncontradoException("Não há prestadores disponíveis para o medicamento buscado.");
                }
            }
        }

        return true;
    }

}
