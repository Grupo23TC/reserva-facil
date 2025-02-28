package br.com.fiap.hackathon.reservafacil.service;

import br.com.fiap.hackathon.reservafacil.util.PrestadorUtil;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.CadastrarPrestadorRequestDTO;
import br.com.fiap.hackathon.reservafacil.model.dto.prestador.PrestadorResponseDTO;
import br.com.fiap.hackathon.reservafacil.service.impl.PrestadorServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PrestadorServiceTest {

    @Mock
    private PrestadorServiceImpl service;

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
    class BuscarPrestadores {
        @Test
        void deveListarProdutosPaginados() {
            PrestadorResponseDTO prestador1 = PrestadorUtil.gerarPrestadorResponse();
            PrestadorResponseDTO prestador2 = PrestadorUtil.gerarPrestadorResponse();
            PrestadorResponseDTO prestador3 = PrestadorUtil.gerarPrestadorResponse();
            List<PrestadorResponseDTO> listaDePrestadores = new ArrayList<>(Arrays.asList(prestador1, prestador2, prestador3));

            Pageable pageRequest = PageRequest.of(0, 10);
            Page<PrestadorResponseDTO> prestadoresPaginados = new PageImpl<>(listaDePrestadores, pageRequest, listaDePrestadores.size());

            when(service.listarPrestadores(any(Pageable.class))).thenReturn(prestadoresPaginados);

            Page<PrestadorResponseDTO> prestadores = service.listarPrestadores(pageRequest);

            assertThat(prestadores)
                    .isNotNull()
                    .isNotEmpty()
                    .isInstanceOf(Page.class)
                    .hasSize(listaDePrestadores.size());

            assertThat(prestadores.getContent())
                    .containsExactlyElementsOf(listaDePrestadores);

            verify(service, times(1)).listarPrestadores(any(Pageable.class));
        }

        @Test
        void deveBuscarPrestadorPorLocalidade() {
            PrestadorResponseDTO prestadorResponse = PrestadorUtil.gerarPrestadorResponse();
            PrestadorResponseDTO prestadorResponse2 = PrestadorUtil.gerarPrestadorResponse();
            PrestadorResponseDTO prestadorResponse3 = PrestadorUtil.gerarPrestadorResponse();

            List<PrestadorResponseDTO> listaDePrestadores = new ArrayList<>(Arrays.asList(prestadorResponse, prestadorResponse2, prestadorResponse3));

            String localidade = "teste";

            when(service.buscarPrestadorPorLocalidade(anyString())).thenReturn(listaDePrestadores);

            List<PrestadorResponseDTO> prestadorBuscado = service.buscarPrestadorPorLocalidade(localidade);

            assertThat(prestadorBuscado)
                    .isNotNull();

            assertThat(prestadorBuscado.get(0).tipoPrestador())
                    .isEqualTo(prestadorResponse.tipoPrestador());

            verify(service, times(1)).buscarPrestadorPorLocalidade(localidade);
        }

        @Test
        void deveBuscarPrestadorPorMedicamento() {
            PrestadorResponseDTO prestadorResponse = PrestadorUtil.gerarPrestadorResponse();
            PrestadorResponseDTO prestadorResponse2 = PrestadorUtil.gerarPrestadorResponse();
            PrestadorResponseDTO prestadorResponse3 = PrestadorUtil.gerarPrestadorResponse();

            List<PrestadorResponseDTO> listaDePrestadores = new ArrayList<>(Arrays.asList(prestadorResponse, prestadorResponse2, prestadorResponse3));

            String medicamento = "medicamento";

            when(service.buscarPrestadoresPorMedicamentoDisponivel(anyString())).thenReturn(listaDePrestadores);

            List<PrestadorResponseDTO> prestadorBuscado = service.buscarPrestadoresPorMedicamentoDisponivel(medicamento);

            assertThat(prestadorBuscado)
                    .isNotNull();

            assertThat(prestadorBuscado.get(0).tipoPrestador())
                    .isEqualTo(prestadorResponse.tipoPrestador());

            verify(service, times(1)).buscarPrestadoresPorMedicamentoDisponivel(medicamento);
        }

        @Test
        void deveBuscarPrestadorPorMedicamentoELocalidade() {
            PrestadorResponseDTO prestadorResponse = PrestadorUtil.gerarPrestadorResponse();
            PrestadorResponseDTO prestadorResponse2 = PrestadorUtil.gerarPrestadorResponse();
            PrestadorResponseDTO prestadorResponse3 = PrestadorUtil.gerarPrestadorResponse();

            List<PrestadorResponseDTO> listaDePrestadores = new ArrayList<>(Arrays.asList(prestadorResponse, prestadorResponse2, prestadorResponse3));

            String medicamento = "medicamento";
            String localidade = "teste";

            when(service.buscarPrestadoresPorMedicamentoELocalidade(anyString(), anyString())).thenReturn(listaDePrestadores);

            List<PrestadorResponseDTO> prestadorBuscado = service.buscarPrestadoresPorMedicamentoELocalidade(medicamento, localidade);

            assertThat(prestadorBuscado)
                    .isNotNull();

            assertThat(prestadorBuscado.get(0).tipoPrestador())
                    .isEqualTo(prestadorResponse.tipoPrestador());

            verify(service, times(1)).buscarPrestadoresPorMedicamentoELocalidade(medicamento, localidade);
        }
    }

    @Nested
    class CadastrarPrestador {
        @Test
        void devePermitirCadastrarPrestador() {
            CadastrarPrestadorRequestDTO request = PrestadorUtil.gerarCadastrarPrestadorRequestDTO();
            PrestadorResponseDTO prestador = PrestadorUtil.gerarPrestadorResponse();

            when(service.cadastrarPrestador(any(CadastrarPrestadorRequestDTO.class))).thenReturn(prestador);

            PrestadorResponseDTO prestadorCriado = service.cadastrarPrestador(request);

            assertThat(prestadorCriado)
                    .isNotNull()
                    .isInstanceOf(PrestadorResponseDTO.class);

            verify(service, times(1)).cadastrarPrestador(any(CadastrarPrestadorRequestDTO.class));
        }
    }
}
