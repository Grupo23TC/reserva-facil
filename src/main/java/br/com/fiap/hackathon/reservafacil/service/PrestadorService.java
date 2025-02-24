package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.exception.prestador.PrestadorNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.mapper.PrestadorMapper;
import br.com.fiap.hackathon.reservafacil.model.Prestador;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.CadastrarPrestadorRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.PrestadorResponseDTO;
import br.com.fiap.hackathon.reservafacil.repository.PrestadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrestadorService {
    private final PrestadorRepository prestadorRepository;

    public Page<PrestadorResponseDTO> listarPrestadores(Pageable pageable) {
        return prestadorRepository.findAll(pageable).map(PrestadorMapper::toPrestadorResponseDTO);
    }

    public PrestadorResponseDTO cadastrarPrestador(CadastrarPrestadorRequestDTO prestadorRequestDTO) {
        Prestador prestador = new Prestador();
        prestador.setNome(prestadorRequestDTO.nome());
        prestador.setNomeFantasia(prestadorRequestDTO.nomeFantasia());
        prestador.setEndereco(prestadorRequestDTO.endereco());
        prestador.setTipoPrestadorEnum(prestadorRequestDTO.tipoPrestador());

        return PrestadorMapper.toPrestadorResponseDTO(prestadorRepository.save(prestador));
    }

    public List<PrestadorResponseDTO> buscarPrestadorPorLocalidade(String localidade) {
        return prestadorRepository.findByLogradouro(localidade).stream().map(PrestadorMapper::toPrestadorResponseDTO).toList();
    }

    public Prestador buscarPrestadorPorId(UUID id) {
        return prestadorRepository.findById(id).orElseThrow(() -> new PrestadorNaoEncontradoException("Prestador de id: " + id + " não encontrado."));
    }
}
