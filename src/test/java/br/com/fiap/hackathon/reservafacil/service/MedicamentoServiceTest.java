package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.exception.medicamento.MedicamentoNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.prestador.PrestadorNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.model.Medicamento;
import br.com.fiap.hackathon.reservafacil.model.Operador;
import br.com.fiap.hackathon.reservafacil.model.Prestador;
import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.AtualizarMedicamentoRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.medicamento.MedicamentoResponseDTO;
import br.com.fiap.hackathon.reservafacil.repository.MedicamentoRepository;
import br.com.fiap.hackathon.reservafacil.security.SecurityService;
import br.com.fiap.hackathon.reservafacil.service.impl.MedicamentoServiceImpl;
import br.com.fiap.hackathon.reservafacil.service.impl.OperadorServiceImpl;
import br.com.fiap.hackathon.reservafacil.util.MedicamentoUtil;
import br.com.fiap.hackathon.reservafacil.util.PrestadorUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.hackathon.reservafacil.util.OperadorUtil.gerarOperador;
import static br.com.fiap.hackathon.reservafacil.util.UsuarilUtil.gerarUsuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MedicamentoServiceTest {

    @Mock
    private SecurityService securityService;

    @Mock
    private OperadorService operadorService;

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private PrestadorService prestadorService;

    @InjectMocks
    private MedicamentoServiceImpl medicamentoService;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class CadastrarMedicamento {

        @Test
        void deveCadastrarMedicamento() {
            Usuario usuario = gerarUsuario();
            Operador operador = gerarOperador();
            Prestador prestador = PrestadorUtil.gerarPrestador();

            when(securityService.obterUsuarioLogado()).thenReturn(usuario);
            when(operadorService.buscarPorCns(anyString())).thenReturn(operador);
            when(medicamentoRepository.save(any(Medicamento.class))).thenReturn(MedicamentoUtil.gerarMedicamento());

            var result = medicamentoService.cadastrarMedicamento(MedicamentoUtil.gerarCadastrarMedicamentoRequestDTO());

            assertThat(result)
                    .isNotNull()
                    .isInstanceOf(MedicamentoResponseDTO.class);
            assertThat(result.prestador())
                    .isEqualTo(prestador.getNome());

            verify(securityService, times(1)).obterUsuarioLogado();
            verify(operadorService, times(1)).buscarPorCns(anyString());
            verify(medicamentoRepository, times(1)).save(any(Medicamento.class));
        }
    }

    @Nested
    class BuscarMedicamento {

        @Test
        void deveBuscarMedicamento() {
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            when(medicamentoRepository.findById(any(UUID.class))).thenReturn(Optional.of(medicamento));

            var result = medicamentoService.buscarMedicamento(UUID.randomUUID());

            assertThat(result)
                    .isInstanceOf(Medicamento.class)
                    .isNotNull();
            assertThat(result.getId())
                    .isEqualTo(medicamento.getId());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMedicamento_MedicamentoNaoEncontrado() {
            UUID id = UUID.randomUUID();
            String message = "Medicamento id: " + id + " não encontrado.";
            when(medicamentoRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> medicamentoService.buscarMedicamento(id))
                    .isInstanceOf(MedicamentoNaoEncontradoException.class)
                    .hasMessage(message);
        }

        @Test
        void deveBuscarMedicamentoDTO() {
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento();
            when(medicamentoRepository.findById(any(UUID.class))).thenReturn(Optional.of(medicamento));

            var result = medicamentoService.buscarMedicamentoDTO(UUID.randomUUID());

            assertThat(result)
                    .isInstanceOf(MedicamentoResponseDTO.class)
                    .isNotNull();
            assertThat(result.id())
                    .isEqualTo(medicamento.getId());
        }

        @Test
        void deveListarMedicamentos() {
            List<Medicamento> medicamentos = List.of(MedicamentoUtil.gerarMedicamento(),MedicamentoUtil.gerarMedicamento());
            when(medicamentoRepository.findAll()).thenReturn(medicamentos);

            var result = medicamentoService.listarMedicamentos();

            assertThat(result)
                    .hasSize(2);
        }
    }

    @Nested
    class EditarMedicamento {

        @Test
        void deveEditarMedicamento() {
            UUID idPrestador = UUID.randomUUID();
            Usuario usuario = gerarUsuario();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento(idPrestador);
            Operador operador = gerarOperador(idPrestador);

            when(securityService.obterUsuarioLogado()).thenReturn(usuario);
            when(operadorService.buscarPorCns(anyString())).thenReturn(operador);
            when(medicamentoRepository.findById(any(UUID.class))).thenReturn(Optional.of(medicamento));
            when(medicamentoRepository.save(any(Medicamento.class))).thenReturn(medicamento);

            AtualizarMedicamentoRequestDTO dto = MedicamentoUtil.gerarAtualizarMedicamentoRequestDTO();
            var result = medicamentoService.atualizarMedicamento(medicamento.getId(), dto, false);

            assertThat(result)
                    .isInstanceOf(MedicamentoResponseDTO.class)
                    .isNotNull();
            assertThat(result.id()).isEqualTo(medicamento.getId());
            assertThat(result.nome()).isEqualTo(dto.nome());
            assertThat(result.validade()).isEqualTo(dto.validade().toString());
            assertThat(result.quantidade()).isEqualTo(dto.quantidade());
        }

        @Test
        void deveGerarExceca_QuandoEditarMedicamento_MedicamentoNaoEncontrado() {
            UUID id = UUID.randomUUID();
            String message = "Medicamento id: " + id + " não encontrado.";
            when(medicamentoRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            AtualizarMedicamentoRequestDTO dto = MedicamentoUtil.gerarAtualizarMedicamentoRequestDTO();
            assertThatThrownBy(() -> medicamentoService.atualizarMedicamento(id, dto, false))
                    .isInstanceOf(MedicamentoNaoEncontradoException.class)
                    .hasMessage(message);
        }
    }

    @Nested
    class ExcluirMedicamento {

        @Test
        void deveExcluirMedicamento() {
            UUID idPrestador = UUID.randomUUID();
            Usuario usuario = gerarUsuario();
            Medicamento medicamento = MedicamentoUtil.gerarMedicamento(idPrestador);
            Operador operador = gerarOperador(idPrestador);

            when(securityService.obterUsuarioLogado()).thenReturn(usuario);
            when(operadorService.buscarPorCns(anyString())).thenReturn(operador);
            when(medicamentoRepository.findById(any(UUID.class))).thenReturn(Optional.of(medicamento));
            doNothing().when(medicamentoRepository).deleteById(any(UUID.class));

            medicamentoService.excluirMedicamento(UUID.randomUUID());
        }

        @Test
        void deveGerarExcecao_QuandoExcluirMedicamento_MedicamentoNaoEncontrado() {
            UUID id = UUID.randomUUID();
            String message = "Medicamento id: " + id + " não encontrado.";
            when(medicamentoRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> medicamentoService.excluirMedicamento(id))
                    .isInstanceOf(MedicamentoNaoEncontradoException.class)
                    .hasMessage(message);

        }
    }
}
