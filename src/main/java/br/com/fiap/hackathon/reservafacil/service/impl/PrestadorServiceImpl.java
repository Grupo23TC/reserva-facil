package br.com.fiap.hackathon.reservafacil.service.impl;

import br.com.fiap.hackathon.reservafacil.exception.prestador.PrestadorNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.mapper.PrestadorMapper;
import br.com.fiap.hackathon.reservafacil.model.Operador;
import br.com.fiap.hackathon.reservafacil.model.Prestador;
import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.AtualizarPrestadorRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.CadastrarPrestadorRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.PrestadorResponseDTO;
import br.com.fiap.hackathon.reservafacil.repository.PrestadorRepository;
import br.com.fiap.hackathon.reservafacil.security.SecurityService;
import br.com.fiap.hackathon.reservafacil.service.OperadorService;
import br.com.fiap.hackathon.reservafacil.service.PrestadorService;
import br.com.fiap.hackathon.reservafacil.utils.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrestadorServiceImpl implements PrestadorService {
    private final PrestadorRepository prestadorRepository;
    private final SecurityService securityService;
    private final OperadorService operadorService;

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
    public PrestadorResponseDTO atualizarPrestador(AtualizarPrestadorRequestDTO prestadorRequestDTO) {
        Prestador prestador = obterPrestadorDoUsuarioLogado();

        prestador.setNome(prestadorRequestDTO.nome());
        prestador.setNomeFantasia(prestadorRequestDTO.nomeFantasia());
        prestador.setEndereco(prestadorRequestDTO.endereco());
        prestador.setTipoPrestadorEnum(prestadorRequestDTO.tipoPrestador());


        return PrestadorMapper.toPrestadorResponseDTO(prestadorRepository.save(prestador));
    }

    @Override
    @Transactional
    public void excluirPrestador() {
        Prestador prestador = obterPrestadorDoUsuarioLogado();

        prestadorRepository.delete(prestador);
    }

    @Override
    @Transactional
    public Prestador buscarPrestadorPorId(UUID id) {
        return prestadorRepository.findById(id).orElseThrow(() -> new PrestadorNaoEncontradoException("Prestador de id: " + id + " não encontrado."));
    }

    @Override
    @Transactional
    public List<PrestadorResponseDTO> buscarPrestadorPorCidade(String cidade) {
        cidade = StringUtils.addLikeQueryCondition(cidade);
        return prestadorRepository.findByCidade(cidade).stream().map(PrestadorMapper::toPrestadorResponseDTO).toList();
    }

    @Override
    @Transactional
    public List<PrestadorResponseDTO> buscarPrestadoresPorMedicamentoECidade(String cidade, String nomeMedicamento) {
        cidade = StringUtils.addLikeQueryCondition(cidade);
        nomeMedicamento = StringUtils.addLikeQueryCondition(nomeMedicamento);
        List<Prestador> prestadores = prestadorRepository.findByCidadeAndNomeMedicamento(cidade, nomeMedicamento);
        return PrestadorMapper.toPrestadorResponseDTOList(prestadores);
    }

    @Override
    @Transactional
    public List<PrestadorResponseDTO> buscarPrestadoresPorMedicamentoDisponivel(String nomeMedicamento) {
        nomeMedicamento = "%" + nomeMedicamento + "%";
        List<Prestador> prestadores = prestadorRepository.findByNomeMedicamento(nomeMedicamento);

        return PrestadorMapper.toPrestadorResponseDTOList(prestadores);
    }

    private Prestador obterPrestadorDoUsuarioLogado() {
        Usuario usuarioLogado = securityService.obterUsuarioLogado();
        Operador operador = operadorService.buscarPorCns(usuarioLogado.getCns());

        return operador.getPrestador();
    }
}
