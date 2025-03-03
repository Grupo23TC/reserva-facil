package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.model.Prestador;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.AtualizarPrestadorRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.CadastrarPrestadorRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.PrestadorResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PrestadorService {

    Page<PrestadorResponseDTO> listarPrestadores(Pageable pageable);

    PrestadorResponseDTO cadastrarPrestador(CadastrarPrestadorRequestDTO prestadorRequestDTO);

    PrestadorResponseDTO atualizarPrestador(AtualizarPrestadorRequestDTO prestadorRequestDTO);
    Prestador buscarPrestadorPorId(UUID id);

    void excluirPrestador();

    List<PrestadorResponseDTO> buscarPrestadorPorCidade(String localidade);

    List<PrestadorResponseDTO> buscarPrestadoresPorMedicamentoDisponivel(String nomeMedicamento);
    List<PrestadorResponseDTO> buscarPrestadoresPorMedicamentoECidade(String localidade, String nomeMedicamento);
}
