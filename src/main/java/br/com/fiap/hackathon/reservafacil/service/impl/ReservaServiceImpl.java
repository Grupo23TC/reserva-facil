package br.com.fiap.hackathon.reservafacil.service.impl;

import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoEsgotadoException;
import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoNaoPertencePrestadorException;
import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoRestritoException;
import br.com.fiap.hackathon.reservafacil.exception.reserva.DataReservaInvalidaException;
import br.com.fiap.hackathon.reservafacil.exception.reserva.DataReservaNaoDisponivelException;
import br.com.fiap.hackathon.reservafacil.exception.reserva.ReservaNaoEncontradaException;
import br.com.fiap.hackathon.reservafacil.mapper.ReservaMapper;
import br.com.fiap.hackathon.reservafacil.model.*;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.AtualizarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.reserva.CadastrarReservaRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.reserva.ReservaResponseDTO;
import br.com.fiap.hackathon.reservafacil.model.enums.TipoMedicamentoEnum;
import br.com.fiap.hackathon.reservafacil.repository.ReservaRepository;
import br.com.fiap.hackathon.reservafacil.security.SecurityService;
import br.com.fiap.hackathon.reservafacil.service.BeneficiarioService;
import br.com.fiap.hackathon.reservafacil.service.MedicamentoService;
import br.com.fiap.hackathon.reservafacil.service.PrestadorService;
import br.com.fiap.hackathon.reservafacil.service.ReservaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static br.com.fiap.hackathon.reservafacil.utils.HorariosComerciaisUtils.gerarIntervalosComerciais;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private BeneficiarioService beneficiarioService;

    @Autowired
    private PrestadorService prestadorService;

    @Autowired
    private MedicamentoService medicamentoService;

    @Autowired
    private SecurityService securityService;

    @Override
    @Transactional
    public ReservaResponseDTO cadastrarReserva(CadastrarReservaRequestDTO dto) {

        if (dto.dataReserva() == null || dto.dataReserva().isBefore(LocalDateTime.now())) {
            throw new DataReservaInvalidaException("Data da reserva precisa ser futura, tente novamente.");
        }

        // Validação dos minutos (devem ser 00, 15, 30 ou 45)
        int minutos = dto.dataReserva().getMinute();
        if (minutos != 0 && minutos != 15 && minutos != 30 && minutos != 45) {
            throw new DataReservaInvalidaException("Os minutos da data reservada devem ser 00, 15, 30 ou 45.");
        }

        Reserva reserva = new Reserva();
        reserva.setDataReserva(dto.dataReserva());

        Usuario usuarioLogado = securityService.obterUsuarioLogado();
        Beneficiario beneficiario = beneficiarioService.buscarPorCns(usuarioLogado.getCns());
        reserva.setBeneficiario(beneficiario);

        Prestador prestador = prestadorService.buscarPrestadorPorId(dto.idPrestador());
        reserva.setPrestador(prestador);

        List<Reserva> reservas = reservaRepository.findByPrestadorAndDataReserva(dto.idPrestador(), dto.dataReserva());
        if (reservas != null && !reservas.isEmpty() && reservas.size() >= 3) { // o Prestador pode receber até 3 beneficiarios em um determinado horario
            throw new DataReservaNaoDisponivelException("Data/hora escolhida não esta disponível para o prestador: " + prestador.getNome() + ", tente novamente.");
        }

        Medicamento medicamento = medicamentoService.buscarMedicamento(dto.idMedicamento());
        reserva.setMedicamento(medicamento);

        if (medicamento.getQuantidade() == 0) {
            throw new MedicamentoEsgotadoException("Medicamento solicitado está esgotado");
        }

        if (medicamento.getPrestador().getId() != prestador.getId()) {
            throw new MedicamentoNaoPertencePrestadorException("O medicamento de id:" + medicamento.getId() + " não pertence ao prestador " + prestador.getNome() + ".");
        }

        if (!TipoMedicamentoEnum.SEM_TARJA.equals(medicamento.getTipoMedicamentoEnum()) &&
                !TipoMedicamentoEnum.TARJA_AMARELA.equals(medicamento.getTipoMedicamentoEnum()) &&
                !beneficiario.getTipoMedicamento().equals(medicamento.getTipoMedicamentoEnum())) {
            throw new MedicamentoRestritoException("Este medicamento é controlado e só pode ser reservado por beneficiários com receita específica");
        }

        // Decrementar estoque
        AtualizarMedicamentoRequestDTO medDTO = new AtualizarMedicamentoRequestDTO(null, medicamento.getQuantidade() - 1, null);
        medicamentoService.atualizarMedicamento(medicamento.getId(), medDTO, true);

        return ReservaMapper.toReservaResponseDTO(reservaRepository.save(reserva));
    }

    @Override
    @Transactional
    public ReservaResponseDTO buscarReserva(UUID id) {
        return ReservaMapper.toReservaResponseDTO(reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNaoEncontradaException("Reserva de id: " + id + " não encontrada.")));
    }

    @Override
    @Transactional
    public List<ReservaResponseDTO> listarReservas() {
        return reservaRepository.findAll().stream().map(ReservaMapper::toReservaResponseDTO).toList();
    }

    @Override
    @Transactional
    public List<ReservaResponseDTO> listarReservasBeneficiario(String cns) {
        return reservaRepository.findAllByBeneficiario(cns).stream().map(ReservaMapper::toReservaResponseDTO).toList();
    }

    @Override
    @Transactional
    public List<ReservaResponseDTO> listarReservasPrestador(UUID prestadorId) {
        return reservaRepository.findAllByPrestadorId(prestadorId).stream().map(ReservaMapper::toReservaResponseDTO).toList();
    }

    @Override
    @Transactional
    public List<String> listarHorariosDisponiveisPeriodoPrestador(UUID prestadorId, LocalDateTime dataInicial, LocalDateTime dataFinal) {

        prestadorService.buscarPrestadorPorId(prestadorId);

        List<LocalDateTime> intervalos = gerarIntervalosComerciais(dataInicial, dataFinal);

        List<LocalDateTime> horariosNaoDisponiveis = reservaRepository.findByPrestadorIdAndNaoDisponivelPeriodo(prestadorId, dataInicial, dataFinal);

        return intervalos.stream().filter(i -> !horariosNaoDisponiveis.contains(i)).map(LocalDateTime::toString).toList();
    }

    @Override
    @Transactional
    public void deletarReserva(UUID id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNaoEncontradaException("Reserva de id: " + id + " não encontrada."));

        // Incrementar estoque
        AtualizarMedicamentoRequestDTO medDTO = new AtualizarMedicamentoRequestDTO(null, reserva.getMedicamento().getQuantidade() + 1, null);
        medicamentoService.atualizarMedicamento(reserva.getMedicamento().getId(), medDTO, true);

        reservaRepository.deleteById(id);
    }

}
