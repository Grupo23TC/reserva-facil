package br.com.fiap.hackathon.reservafacil.controller;

import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.handler.GlobalExceptionHandler;
import br.com.fiap.hackathon.reservafacil.exception.operador.OperadorCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.operador.OperadorNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.AcessoNegadoException;
import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.Operador;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.BeneficiarioResponse;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.operador.CadastrarOperadorRequest;
import br.com.fiap.hackathon.reservafacil.service.OperadorService;
import br.com.fiap.hackathon.reservafacil.service.impl.BeneficiarioServiceImpl;
import br.com.fiap.hackathon.reservafacil.service.impl.OperadorServiceImpl;
import br.com.fiap.hackathon.reservafacil.util.JsonStringHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static br.com.fiap.hackathon.reservafacil.util.BeneficiarioUtil.*;
import static br.com.fiap.hackathon.reservafacil.util.OperadorUtil.gerarCadastrarOperadorRequest;
import static br.com.fiap.hackathon.reservafacil.util.OperadorUtil.gerarOperador;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OperadorControllerTest {
    private static final String ACESSO_NEGADO = "Você não pode ter acesso ou alterar os dados de outros beneficiários";
    private static final String OPERADOR_NAO_ENCONTRADO = "Beneficiário não encontrado";
    private static final String OPERADOR_JA_EXISTE = "Beneficiário já cadastrado";

    @Mock
    private OperadorServiceImpl service;

    @InjectMocks
    private OperadorController controller;

    private MockMvc mockMvc;

    private AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(pageableResolver)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                })
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class CadastrarOperador {
        @Test
        void deveCadastrarOperador() throws Exception {
            CadastrarOperadorRequest request = gerarCadastrarOperadorRequest();
            Operador operador = gerarOperador();

            when(service.cadastrar(any(CadastrarOperadorRequest.class))).thenReturn(operador);

            mockMvc.perform(
                            post("/api/v1/operadores")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonStringHelper.asJsonString(request))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cns").value(operador.getCns()))
                    .andExpect(jsonPath("$.nome").value(operador.getNome()))
                    .andExpect(jsonPath("$.cargo").value(operador.getCargo()));

            verify(service, times(1)).cadastrar(any(CadastrarOperadorRequest.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarOperador_CnsJaCadastrado() throws Exception {
            CadastrarOperadorRequest request = gerarCadastrarOperadorRequest("234567890123456");

            when(service.cadastrar(any(CadastrarOperadorRequest.class)))
                    .thenThrow(new OperadorCadastradoException(OPERADOR_JA_EXISTE));

            mockMvc.perform(
                            post("/api/v1/operadores")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonStringHelper.asJsonString(request))
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.erro").value(OPERADOR_JA_EXISTE))
                    .andExpect(jsonPath("$.rota").value("/api/v1/operadores"));

            verify(service, times(1)).cadastrar(any(CadastrarOperadorRequest.class));
        }
    }

    @Nested
    class BuscarOperador {
        @Test
        void deveBuscarPorCns() throws Exception {
            String cns = "234567890123456";
            Operador operador = gerarOperador();

            when(service.buscarPorCns(anyString())).thenReturn(operador);

            mockMvc.perform(get("/api/v1/operadores/{cns}", cns))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cns").value(cns))
                    .andExpect(jsonPath("$.nome").value(operador.getNome()))
                    .andExpect(jsonPath("$.cargo").value(operador.getCargo()));

            verify(service, times(1)).buscarPorCns(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPorCns_CnsNaoEncontrado() throws Exception {
            String cns = "654987123065482";

            when(service.buscarPorCns(anyString()))
                    .thenThrow(new OperadorNaoEncontradoException(OPERADOR_NAO_ENCONTRADO));

            mockMvc.perform(get("/api/v1/operadores/{cns}", cns))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.erro").value(OPERADOR_NAO_ENCONTRADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/operadores/"+cns));

            verify(service, times(1)).buscarPorCns(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPorCns_AcessoNegadoADadosDeOutrosUsuarios() throws Exception {
            String cns = "654987123065482";

            when(service.buscarPorCns(anyString()))
                    .thenThrow(new AcessoNegadoException(ACESSO_NEGADO));

            mockMvc.perform(get("/api/v1/operadores/{cns}", cns))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                    .andExpect(jsonPath("$.erro").value(ACESSO_NEGADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/operadores/"+cns));

            verify(service, times(1)).buscarPorCns(anyString());
        }
    }

    @Nested
    class AtivarOperador {
        @Test
        void deveAtivarOperador() throws Exception {
            String cns = "234567890123456";
            Operador operador = gerarOperador();

            when(service.ativar(anyString())).thenReturn(operador);

            mockMvc.perform(patch("/api/v1/operadores/{cns}", cns))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cns").value(cns))
                    .andExpect(jsonPath("$.nome").value(operador.getNome()))
                    .andExpect(jsonPath("$.ativo").value(operador.getAtivo()))
                    .andExpect(jsonPath("$.cargo").value(operador.getCargo()));

            verify(service, times(1)).ativar(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoAtivarOperador_CnsNaoEncontrado() throws Exception {
            String cns = "654987123065482";

            when(service.ativar(anyString()))
                    .thenThrow(new OperadorNaoEncontradoException(OPERADOR_NAO_ENCONTRADO));

            mockMvc.perform(patch("/api/v1/operadores/{cns}", cns))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.erro").value(OPERADOR_NAO_ENCONTRADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/operadores/"+cns));

            verify(service, times(1)).ativar(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoAtivarOperador_AcessoNegado() throws Exception {
            String cns = "654987123065482";

            when(service.ativar(anyString()))
                    .thenThrow(new AcessoNegadoException(ACESSO_NEGADO));

            mockMvc.perform(patch("/api/v1/operadores/{cns}", cns))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                    .andExpect(jsonPath("$.erro").value(ACESSO_NEGADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/operadores/"+cns));

            verify(service, times(1)).ativar(anyString());
        }
    }

    @Nested
    class DesativarBeneficiario {
        @Test
        void deveDesativarOperador() throws Exception {
            String cns = "654987123065482";
            Operador operador = gerarOperador(cns, false);

            when(service.desativar(anyString())).thenReturn(operador);

            mockMvc.perform(delete("/api/v1/operadores/{cns}", cns))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cns").value(cns))
                    .andExpect(jsonPath("$.nome").value(operador.getNome()))
                    .andExpect(jsonPath("$.ativo").value(operador.getAtivo()))
                    .andExpect(jsonPath("$.cargo").value(operador.getCargo()));

            verify(service, times(1)).desativar(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoDesativarOperador_CnsNaoEncontrado() throws Exception {
            String cns = "654987123065482";

            when(service.desativar(anyString()))
                    .thenThrow(new OperadorNaoEncontradoException(OPERADOR_NAO_ENCONTRADO));

            mockMvc.perform(delete("/api/v1/operadores/{cns}", cns))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.erro").value(OPERADOR_NAO_ENCONTRADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/operadores/"+cns));

            verify(service, times(1)).desativar(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoDesativarOperador_AcessoNegado() throws Exception {
            String cns = "654987123065482";

            when(service.desativar(anyString()))
                    .thenThrow(new AcessoNegadoException(ACESSO_NEGADO));

            mockMvc.perform(delete("/api/v1/operadores/{cns}", cns))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                    .andExpect(jsonPath("$.erro").value(ACESSO_NEGADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/operadores/"+cns));

            verify(service, times(1)).desativar(anyString());
        }
    }
}
