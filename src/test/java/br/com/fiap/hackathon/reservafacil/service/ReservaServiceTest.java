package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoEsgotadoException;
import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoNaoPertencePrestadorException;
import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoRestritoException;
import br.com.fiap.hackathon.reservafacil.exception.prestador.PrestadorNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.reserva.DataReservaInvalidaException;
import br.com.fiap.hackathon.reservafacil.exception.reserva.DataReservaNaoDisponivelException;
import br.com.fiap.hackathon.reservafacil.exception.reserva.ReservaNaoEncontradaException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.AcessoNegadoException;
import br.com.fiap.hackathon.reservafacil.model.*;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.AtualizarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.reserva.CadastrarReservaRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.reserva.ReservaResponseDTO;
import br.com.fiap.hackathon.reservafacil.repository.ReservaRepository;
import br.com.fiap.hackathon.reservafacil.security.SecurityService;
import br.com.fiap.hackathon.reservafacil.service.impl.ReservaServiceImpl;
import br.com.fiap.hackathon.reservafacil.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ReservaServiceTest {

    @Mock
    private SecurityService securityService;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private BeneficiarioService beneficiarioService;

    @Mock
    private PrestadorService prestadorService;

    @Mock
    private MedicamentoService medicamentoService;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    private final String DATA_PASSADA = "Data da reserva precisa ser futura, tente novamente.";
    private final String DATA_INVALIDA = "Os minutos da data reservada devem ser 00, 15, 30 ou 45.";
    private final String BENEFICIARIO_NAO_ENCONTRADO = "Beneficiário não encontrado";
    private final String BENEFICIARIO_ACESSO_NEGADO = "Você não pode ter acesso ou alterar os dados de outros beneficiários";
    private final String PRESTADOR_NAO_ENCONTRADO = "Prestador de id: " + PrestadorUtil.gerarPrestador().getId() + " não encontrado.";
    private final String DATA_INDISPONIVEL = "Data/hora escolhida não esta disponível para o prestador: " + PrestadorUtil.gerarPrestador().getNome() + ", tente novamente.";
    private final String MEDICAMENTO_NAO_ENCONTRADO = "Medicamento id: " + MedicamentoUtil.gerarMedicamento().getId() + " não encontrado.";
    private final String MEDICAMENTO_ESGOTADO = "Medicamento solicitado está esgotado";
    private final String TIPO_MEDICAMENTO = "Este medicamento é controlado e só pode ser reservado por beneficiários com receita específica";


    @Nested
    class CadastrarReserva {

        @Test
        void devePermitirCadastrarReserva() {
            Reserva reserva = ReservaUtil.gerarReserva();
            Beneficiario beneficiario = BeneficiarioUtil.gerarBeneficiario();
            Prestador prestador = PrestadorUtil.gerarPrestador();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            medicamento.setPrestador(prestador);
            medicamento.setTipoMedicamentoEnum(beneficiario.getTipoMedicamento());

            when(securityService.obterUsuarioLogado()).thenReturn(UsuarioUtil.gerarUsuario());
            when(beneficiarioService.buscarPorCns(anyString())).thenReturn(beneficiario);
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenReturn(prestador);
            when(medicamentoService.buscarMedicamento(any(UUID.class))).thenReturn(medicamento);
            when(reservaRepository.findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
            when(medicamentoService.atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean())).thenReturn(MedicamentoUtil.gerarMedicamentoResponseDTO());
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

            var result = reservaService.cadastrarReserva(ReservaUtil.gerarCadastrarReservaRequestDTO());

            assertThat(result)
                    .isInstanceOf(ReservaResponseDTO.class)
                    .isNotNull();
            assertThat(result.id())
                    .isEqualTo(reserva.getId());

            verify(securityService, times(1)).obterUsuarioLogado();
            verify(beneficiarioService, times(1)).buscarPorCns(anyString());
            verify(prestadorService, times(1)).buscarPrestadorPorId(any(UUID.class));
            verify(medicamentoService, times(1)).buscarMedicamento(any(UUID.class));
            verify(reservaRepository, times(1)).findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class));
            verify(medicamentoService, times(1)).atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean());
            verify(reservaRepository, times(1)).save(any(Reserva.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarReserva_DataPassada() {
            Reserva reserva = ReservaUtil.gerarReserva();
            Beneficiario beneficiario = BeneficiarioUtil.gerarBeneficiario();
            Prestador prestador = PrestadorUtil.gerarPrestador();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            medicamento.setPrestador(prestador);
            medicamento.setTipoMedicamentoEnum(beneficiario.getTipoMedicamento());

            when(securityService.obterUsuarioLogado()).thenReturn(UsuarioUtil.gerarUsuario());
            when(beneficiarioService.buscarPorCns(anyString())).thenReturn(beneficiario);
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenReturn(prestador);
            when(medicamentoService.buscarMedicamento(any(UUID.class))).thenReturn(medicamento);
            when(reservaRepository.findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
            when(medicamentoService.atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean())).thenReturn(MedicamentoUtil.gerarMedicamentoResponseDTO());
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

            CadastrarReservaRequestDTO dto = ReservaUtil.gerarCadastrarReservaRequestDTO_DataPassada();
            assertThatThrownBy(() -> reservaService.cadastrarReserva(dto))
                    .hasMessage(DATA_PASSADA)
                    .isInstanceOf(DataReservaInvalidaException.class);

            verify(securityService, never()).obterUsuarioLogado();
            verify(beneficiarioService, never()).buscarPorCns(anyString());
            verify(prestadorService, never()).buscarPrestadorPorId(any(UUID.class));
            verify(medicamentoService, never()).buscarMedicamento(any(UUID.class));
            verify(reservaRepository, never()).findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class));
            verify(medicamentoService, never()).atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean());
            verify(reservaRepository, never()).save(any(Reserva.class));

        }

        @Test
        void deveGerarExcecao_QuandoCadastrarReserva_DataInvalida() {
            Reserva reserva = ReservaUtil.gerarReserva();
            Beneficiario beneficiario = BeneficiarioUtil.gerarBeneficiario();
            Prestador prestador = PrestadorUtil.gerarPrestador();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            medicamento.setPrestador(prestador);
            medicamento.setTipoMedicamentoEnum(beneficiario.getTipoMedicamento());


            when(securityService.obterUsuarioLogado()).thenReturn(UsuarioUtil.gerarUsuario());
            when(beneficiarioService.buscarPorCns(anyString())).thenReturn(beneficiario);
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenReturn(prestador);
            when(medicamentoService.buscarMedicamento(any(UUID.class))).thenReturn(medicamento);
            when(reservaRepository.findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
            when(medicamentoService.atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean())).thenReturn(MedicamentoUtil.gerarMedicamentoResponseDTO());
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

            CadastrarReservaRequestDTO dto = ReservaUtil.gerarCadastrarReservaRequestDTO_DataInvalida();
            assertThatThrownBy(() -> reservaService.cadastrarReserva(dto))
                    .hasMessage(DATA_INVALIDA)
                    .isInstanceOf(DataReservaInvalidaException.class);

            verify(securityService, never()).obterUsuarioLogado();
            verify(beneficiarioService, never()).buscarPorCns(anyString());
            verify(prestadorService, never()).buscarPrestadorPorId(any(UUID.class));
            verify(medicamentoService, never()).buscarMedicamento(any(UUID.class));
            verify(reservaRepository, never()).findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class));
            verify(medicamentoService, never()).atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean());
            verify(reservaRepository, never()).save(any(Reserva.class));
        }


        @Test
        void deveGerarExcecao_QuandoCadastrarReserva_BeneficiarioNaoEncontrado() {
            Reserva reserva = ReservaUtil.gerarReserva();
            Beneficiario beneficiario = BeneficiarioUtil.gerarBeneficiario();
            Prestador prestador = PrestadorUtil.gerarPrestador();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            medicamento.setPrestador(prestador);
            medicamento.setTipoMedicamentoEnum(beneficiario.getTipoMedicamento());

            when(securityService.obterUsuarioLogado()).thenReturn(UsuarioUtil.gerarUsuario());
            when(beneficiarioService.buscarPorCns(anyString())).thenThrow(new BeneficiarioNaoEncontradoException(BENEFICIARIO_NAO_ENCONTRADO));
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenReturn(prestador);
            when(medicamentoService.buscarMedicamento(any(UUID.class))).thenReturn(medicamento);
            when(reservaRepository.findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
            when(medicamentoService.atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean())).thenReturn(MedicamentoUtil.gerarMedicamentoResponseDTO());
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

            CadastrarReservaRequestDTO dto = ReservaUtil.gerarCadastrarReservaRequestDTO();
            assertThatThrownBy(() -> reservaService.cadastrarReserva(dto))
                    .hasMessage(BENEFICIARIO_NAO_ENCONTRADO)
                    .isInstanceOf(BeneficiarioNaoEncontradoException.class);

            verify(securityService, times(1)).obterUsuarioLogado();
            verify(beneficiarioService, times(1)).buscarPorCns(anyString());
            verify(prestadorService, never()).buscarPrestadorPorId(any(UUID.class));
            verify(medicamentoService, never()).buscarMedicamento(any(UUID.class));
            verify(reservaRepository, never()).findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class));
            verify(medicamentoService, never()).atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean());
            verify(reservaRepository, never()).save(any(Reserva.class));
        }


        @Test
        void deveGerarExcecao_QuandoCadastrarReserva_BeneficiarioDiferente() {
            Reserva reserva = ReservaUtil.gerarReserva();
            Beneficiario beneficiario = BeneficiarioUtil.gerarBeneficiario();
            Prestador prestador = PrestadorUtil.gerarPrestador();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            medicamento.setPrestador(prestador);
            medicamento.setTipoMedicamentoEnum(beneficiario.getTipoMedicamento());

            when(securityService.obterUsuarioLogado()).thenReturn(UsuarioUtil.gerarUsuario());
            when(beneficiarioService.buscarPorCns(anyString())).thenThrow(new AcessoNegadoException(BENEFICIARIO_ACESSO_NEGADO));
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenReturn(prestador);
            when(medicamentoService.buscarMedicamento(any(UUID.class))).thenReturn(medicamento);
            when(reservaRepository.findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
            when(medicamentoService.atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean())).thenReturn(MedicamentoUtil.gerarMedicamentoResponseDTO());
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

            CadastrarReservaRequestDTO dto = ReservaUtil.gerarCadastrarReservaRequestDTO();
            assertThatThrownBy(() -> reservaService.cadastrarReserva(dto))
                    .hasMessage(BENEFICIARIO_ACESSO_NEGADO)
                    .isInstanceOf(AcessoNegadoException.class);

            verify(securityService, times(1)).obterUsuarioLogado();
            verify(beneficiarioService, times(1)).buscarPorCns(anyString());
            verify(prestadorService, never()).buscarPrestadorPorId(any(UUID.class));
            verify(medicamentoService, never()).buscarMedicamento(any(UUID.class));
            verify(reservaRepository, never()).findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class));
            verify(medicamentoService, never()).atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean());
            verify(reservaRepository, never()).save(any(Reserva.class));
        }


        @Test
        void deveGerarExcecao_QuandoCadastrarReserva_PrestadorNaoEncontrado() {
            Reserva reserva = ReservaUtil.gerarReserva();
            Beneficiario beneficiario = BeneficiarioUtil.gerarBeneficiario();
            Prestador prestador = PrestadorUtil.gerarPrestador();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            medicamento.setPrestador(prestador);
            medicamento.setTipoMedicamentoEnum(beneficiario.getTipoMedicamento());

            when(securityService.obterUsuarioLogado()).thenReturn(UsuarioUtil.gerarUsuario());
            when(beneficiarioService.buscarPorCns(anyString())).thenReturn(beneficiario);
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenThrow(new PrestadorNaoEncontradoException(PRESTADOR_NAO_ENCONTRADO));
            when(medicamentoService.buscarMedicamento(any(UUID.class))).thenReturn(medicamento);
            when(reservaRepository.findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
            when(medicamentoService.atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean())).thenReturn(MedicamentoUtil.gerarMedicamentoResponseDTO());
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

            CadastrarReservaRequestDTO dto = ReservaUtil.gerarCadastrarReservaRequestDTO();
            assertThatThrownBy(() -> reservaService.cadastrarReserva(dto))
                    .hasMessage(PRESTADOR_NAO_ENCONTRADO)
                    .isInstanceOf(PrestadorNaoEncontradoException.class);

            verify(securityService, times(1)).obterUsuarioLogado();
            verify(beneficiarioService, times(1)).buscarPorCns(anyString());
            verify(prestadorService, times(1)).buscarPrestadorPorId(any(UUID.class));
            verify(medicamentoService, never()).buscarMedicamento(any(UUID.class));
            verify(reservaRepository, never()).findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class));
            verify(medicamentoService, never()).atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean());
            verify(reservaRepository, never()).save(any(Reserva.class));
        }


        @Test
        void deveGerarExcecao_QuandoCadastrarReserva_DataNaoDisponivel() {
            Reserva reserva = ReservaUtil.gerarReserva();
            Beneficiario beneficiario = BeneficiarioUtil.gerarBeneficiario();
            Prestador prestador = PrestadorUtil.gerarPrestador();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            medicamento.setPrestador(prestador);
            medicamento.setTipoMedicamentoEnum(beneficiario.getTipoMedicamento());

            when(securityService.obterUsuarioLogado()).thenReturn(UsuarioUtil.gerarUsuario());
            when(beneficiarioService.buscarPorCns(anyString())).thenReturn(beneficiario);
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenReturn(prestador);
            when(medicamentoService.buscarMedicamento(any(UUID.class))).thenReturn(medicamento);
            when(reservaRepository.findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class))).thenReturn(List.of(reserva,reserva,reserva,reserva));
            when(medicamentoService.atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean())).thenReturn(MedicamentoUtil.gerarMedicamentoResponseDTO());
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

            CadastrarReservaRequestDTO dto = ReservaUtil.gerarCadastrarReservaRequestDTO();
            assertThatThrownBy(() -> reservaService.cadastrarReserva(dto))
                    .hasMessage(DATA_INDISPONIVEL)
                    .isInstanceOf(DataReservaNaoDisponivelException.class);

            verify(securityService, times(1)).obterUsuarioLogado();
            verify(beneficiarioService, times(1)).buscarPorCns(anyString());
            verify(prestadorService, times(1)).buscarPrestadorPorId(any(UUID.class));
            verify(medicamentoService, never()).buscarMedicamento(any(UUID.class));
            verify(reservaRepository, times(1)).findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class));
            verify(medicamentoService, never()).atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean());
            verify(reservaRepository, never()).save(any(Reserva.class));
        }


        @Test
        void deveGerarExcecao_QuandoCadastrarReserva_MedicamentoNaoEncontrado() {
            Reserva reserva = ReservaUtil.gerarReserva();
            Beneficiario beneficiario = BeneficiarioUtil.gerarBeneficiario();
            Prestador prestador = PrestadorUtil.gerarPrestador();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            medicamento.setPrestador(prestador);
            medicamento.setTipoMedicamentoEnum(beneficiario.getTipoMedicamento());

            when(securityService.obterUsuarioLogado()).thenReturn(UsuarioUtil.gerarUsuario());
            when(beneficiarioService.buscarPorCns(anyString())).thenReturn(beneficiario);
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenReturn(prestador);
            when(medicamentoService.buscarMedicamento(any(UUID.class))).thenThrow(new MedicamentoNaoEncontradoException(MEDICAMENTO_NAO_ENCONTRADO));
            when(reservaRepository.findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
            when(medicamentoService.atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean())).thenReturn(MedicamentoUtil.gerarMedicamentoResponseDTO());
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

            CadastrarReservaRequestDTO dto = ReservaUtil.gerarCadastrarReservaRequestDTO();
            assertThatThrownBy(() -> reservaService.cadastrarReserva(dto))
                    .hasMessage(MEDICAMENTO_NAO_ENCONTRADO)
                    .isInstanceOf(MedicamentoNaoEncontradoException.class);

            verify(securityService, times(1)).obterUsuarioLogado();
            verify(beneficiarioService, times(1)).buscarPorCns(anyString());
            verify(prestadorService, times(1)).buscarPrestadorPorId(any(UUID.class));
            verify(medicamentoService, times(1)).buscarMedicamento(any(UUID.class));
            verify(reservaRepository, times(1)).findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class));
            verify(medicamentoService, never()).atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean());
            verify(reservaRepository, never()).save(any(Reserva.class));
        }


        @Test
        void deveGerarExcecao_QuandoCadastrarReserva_MedicamentoEsgotado() {
            Reserva reserva = ReservaUtil.gerarReserva();
            Beneficiario beneficiario = BeneficiarioUtil.gerarBeneficiario();
            Prestador prestador = PrestadorUtil.gerarPrestador();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            medicamento.setPrestador(prestador);
            medicamento.setTipoMedicamentoEnum(beneficiario.getTipoMedicamento());
            medicamento.setQuantidade(0);

            when(securityService.obterUsuarioLogado()).thenReturn(UsuarioUtil.gerarUsuario());
            when(beneficiarioService.buscarPorCns(anyString())).thenReturn(beneficiario);
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenReturn(prestador);
            when(medicamentoService.buscarMedicamento(any(UUID.class))).thenReturn(medicamento);
            when(reservaRepository.findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
            when(medicamentoService.atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean())).thenReturn(MedicamentoUtil.gerarMedicamentoResponseDTO());
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

            CadastrarReservaRequestDTO dto = ReservaUtil.gerarCadastrarReservaRequestDTO();
            assertThatThrownBy(() -> reservaService.cadastrarReserva(dto))
                    .hasMessage(MEDICAMENTO_ESGOTADO)
                    .isInstanceOf(MedicamentoEsgotadoException.class);

            verify(securityService, times(1)).obterUsuarioLogado();
            verify(beneficiarioService, times(1)).buscarPorCns(anyString());
            verify(prestadorService, times(1)).buscarPrestadorPorId(any(UUID.class));
            verify(medicamentoService, times(1)).buscarMedicamento(any(UUID.class));
            verify(reservaRepository, times(1)).findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class));
            verify(medicamentoService, never()).atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean());
            verify(reservaRepository, never()).save(any(Reserva.class));
        }


        @Test
        void deveGerarExcecao_QuandoCadastrarReserva_MedicamentoNaoPertencePrestador() {
            Reserva reserva = ReservaUtil.gerarReserva();
            Beneficiario beneficiario = BeneficiarioUtil.gerarBeneficiario();
            Prestador prestador = PrestadorUtil.gerarPrestador();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            medicamento.setTipoMedicamentoEnum(beneficiario.getTipoMedicamento());

            when(securityService.obterUsuarioLogado()).thenReturn(UsuarioUtil.gerarUsuario());
            when(beneficiarioService.buscarPorCns(anyString())).thenReturn(beneficiario);
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenReturn(prestador);
            when(medicamentoService.buscarMedicamento(any(UUID.class))).thenReturn(medicamento);
            when(reservaRepository.findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
            when(medicamentoService.atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean())).thenReturn(MedicamentoUtil.gerarMedicamentoResponseDTO());
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

            CadastrarReservaRequestDTO dto = ReservaUtil.gerarCadastrarReservaRequestDTO();
            String message = "O medicamento de id:" + medicamento.getId() + " não pertence ao prestador " + prestador.getNome() + ".";
            assertThatThrownBy(() -> reservaService.cadastrarReserva(dto))
                    .hasMessage(message)
                    .isInstanceOf(MedicamentoNaoPertencePrestadorException.class);

            verify(securityService, times(1)).obterUsuarioLogado();
            verify(beneficiarioService, times(1)).buscarPorCns(anyString());
            verify(prestadorService, times(1)).buscarPrestadorPorId(any(UUID.class));
            verify(medicamentoService, times(1)).buscarMedicamento(any(UUID.class));
            verify(reservaRepository, times(1)).findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class));
            verify(medicamentoService, never()).atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean());
            verify(reservaRepository, never()).save(any(Reserva.class));

        }


        @Test
        void deveGerarExcecao_QuandoCadastrarReserva_MedicamentoRestrito() {
            Reserva reserva = ReservaUtil.gerarReserva();
            Beneficiario beneficiario = BeneficiarioUtil.gerarBeneficiario();
            Prestador prestador = PrestadorUtil.gerarPrestador();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            medicamento.setPrestador(prestador);

            when(securityService.obterUsuarioLogado()).thenReturn(UsuarioUtil.gerarUsuario());
            when(beneficiarioService.buscarPorCns(anyString())).thenReturn(beneficiario);
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenReturn(prestador);
            when(medicamentoService.buscarMedicamento(any(UUID.class))).thenReturn(medicamento);
            when(reservaRepository.findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
            when(medicamentoService.atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean())).thenReturn(MedicamentoUtil.gerarMedicamentoResponseDTO());
            when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

            CadastrarReservaRequestDTO dto = ReservaUtil.gerarCadastrarReservaRequestDTO();
            assertThatThrownBy(() -> reservaService.cadastrarReserva(dto))
                    .hasMessage(TIPO_MEDICAMENTO)
                    .isInstanceOf(MedicamentoRestritoException.class);

            verify(securityService, times(1)).obterUsuarioLogado();
            verify(beneficiarioService, times(1)).buscarPorCns(anyString());
            verify(prestadorService, times(1)).buscarPrestadorPorId(any(UUID.class));
            verify(medicamentoService, times(1)).buscarMedicamento(any(UUID.class));
            verify(reservaRepository, times(1)).findByPrestadorAndDataReserva(any(UUID.class), any(LocalDateTime.class));
            verify(medicamentoService, never()).atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean());
            verify(reservaRepository, never()).save(any(Reserva.class));

        }

    }

    @Nested
    class BuscarReserva {

        @Test
        void devePermitirBuscarReserva() {
            UUID id = UUID.randomUUID();
            Reserva reserva = ReservaUtil.gerarReserva();
            reserva.setId(id);
            when(reservaRepository.findById(any(UUID.class))).thenReturn(Optional.of(reserva));

            var result = reservaService.buscarReserva(id);

            assertThat(result)
                    .isInstanceOf(ReservaResponseDTO.class)
                    .isNotNull();
            assertThat(result.id()).isEqualTo(id);
            verify(reservaRepository, times(1)).findById(any(UUID.class));
        }


        @Test
        void deveGerarExcecao_QuandoBuscarReserva_ReservaNaoEcontrada() {
            UUID id = UUID.randomUUID();
            String message = "Reserva de id: " + id + " não encontrada.";
            when(reservaRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservaService.buscarReserva(id))
                    .hasMessage(message)
                    .isInstanceOf(ReservaNaoEncontradaException.class);
            verify(reservaRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void devePermitirListarReservas() {
            Reserva reserva = ReservaUtil.gerarReserva();
            when(reservaRepository.findAll()).thenReturn(List.of(reserva, reserva, reserva));

            var result = reservaService.listarReservas();

            assertThat(result)
                    .hasSize(3);
        }

        @Test
        void devePermitirListarReservasPorBeneficiario() {
            Reserva reserva = ReservaUtil.gerarReserva();
            when(reservaRepository.findAllByBeneficiario(anyString())).thenReturn(List.of(reserva, reserva, reserva));

            var result = reservaService.listarReservasBeneficiario("123456789012345");

            assertThat(result)
                    .hasSize(3);

        }

        @Test
        void devePermitirListarReservasPorPrestador() {
            Reserva reserva = ReservaUtil.gerarReserva();
            when(reservaRepository.findAllByPrestadorId(any(UUID.class))).thenReturn(List.of(reserva, reserva, reserva));

            var result = reservaService.listarReservasPrestador(UUID.randomUUID());

            assertThat(result)
                    .hasSize(3);

        }

        @Test
        void devePermitirListarHorariosDisponiveisPrestador() {
            Prestador prestador = PrestadorUtil.gerarPrestador();
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenReturn(prestador);
            when(reservaRepository.findByPrestadorIdAndNaoDisponivelPeriodo(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());

            LocalDateTime dataInicial = LocalDateTime.of(2025, 3, 7, 15, 0);
            LocalDateTime dataFinal = LocalDateTime.of(2025, 3, 7, 16, 0);

            var result = reservaService.listarHorariosDisponiveisPeriodoPrestador(UUID.randomUUID(),
                    dataInicial,
                    dataFinal);

            assertThat(result)
                    .hasSize(5);
        }

        @Test
        void devePermitirListarHorariosDisponiveisPrestador_ComUmHorarioNaoDisponivel() {
            Prestador prestador = PrestadorUtil.gerarPrestador();
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenReturn(prestador);
            when(reservaRepository.findByPrestadorIdAndNaoDisponivelPeriodo(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(LocalDateTime.of(2025, 3, 7, 15, 15)));

            LocalDateTime dataInicial = LocalDateTime.of(2025, 3, 7, 15, 0);
            LocalDateTime dataFinal = LocalDateTime.of(2025, 3, 7, 16, 0);

            var result = reservaService.listarHorariosDisponiveisPeriodoPrestador(UUID.randomUUID(),
                    dataInicial,
                    dataFinal);

            assertThat(result)
                    .hasSize(4);
        }


        @Test
        void deveGerarExcecao_QuandoListarHorariosDisponiveisPrestador_PrestadorNaoEncontrado() {
            UUID prestadorId = UUID.randomUUID();
            String message = "Prestador de id: " + prestadorId + " não encontrado.";
            when(prestadorService.buscarPrestadorPorId(any(UUID.class))).thenThrow(new PrestadorNaoEncontradoException(message));

            LocalDateTime dataInicial = LocalDateTime.of(2025, 3, 7, 15, 0);
            LocalDateTime dataFinal = LocalDateTime.of(2025, 3, 7, 16, 0);

            assertThatThrownBy(() -> reservaService.listarHorariosDisponiveisPeriodoPrestador(prestadorId,dataInicial,dataFinal))
                    .isInstanceOf(PrestadorNaoEncontradoException.class)
                    .hasMessage(message);
        }

    }

    @Nested
    class RemoverReservas {
        @Test
        void devePermitirRemoverReservas() {
            Reserva reserva = ReservaUtil.gerarReserva();
            when(reservaRepository.findById(any(UUID.class))).thenReturn(Optional.of(reserva));
            when(medicamentoService.atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean())).thenReturn(MedicamentoUtil.gerarMedicamentoResponseDTO());
            doNothing().when(reservaRepository).deleteById(any(UUID.class));

            reservaService.deletarReserva(UUID.randomUUID());

            verify(reservaRepository, times(1)).findById(any(UUID.class));
            verify(medicamentoService, times(1)).atualizarMedicamento(any(UUID.class), any(AtualizarMedicamentoRequestDTO.class), anyBoolean());
            verify(reservaRepository, times(1)).deleteById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverReservas_ReservaNaoEncontrado() {
            UUID id = UUID.randomUUID();
            String message = "Reserva de id: " + id + " não encontrada.";
            when(reservaRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservaService.deletarReserva(id))
                    .hasMessage(message)
                    .isInstanceOf(ReservaNaoEncontradaException.class);
        }
    }
}
