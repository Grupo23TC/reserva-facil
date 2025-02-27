package br.com.fiap.hackathon.reservafacil.controller;

import br.com.fiap.hackathon.reservafacil.Util.JsonStringHelper;
import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.beneficiario.BeneficiarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.handler.GlobalExceptionHandler;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioNaoIguaisException;
import br.com.fiap.hackathon.reservafacil.model.Beneficiario;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.BeneficiarioResponse;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.service.BeneficiarioService;
import br.com.fiap.hackathon.reservafacil.service.impl.BeneficiarioServiceImpl;
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

import static br.com.fiap.hackathon.reservafacil.Util.BeneficiarioUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BeneficiarioControllerTest {
    private static final String ACESSO_NEGADO = "Você não pode ter acesso ou alterar os dados de outros beneficiários";
    private static final String BENEFICIARIO_NAO_ENCONTRADO = "Beneficiário não encontrado";
    private static final String BENEFICIARIO_JA_EXISTE = "Beneficiário já cadastrado";

    @Mock
    private BeneficiarioServiceImpl service;

    @InjectMocks
    private BeneficiarioController controller;

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
    class CadastrarBeneficiario {
        @Test
        void deveCadastrarBeneficiario() throws Exception {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest("012345678901234", "01234567890");
            Beneficiario beneficiario = gerarBeneficiario();

            when(service.cadastrar(any(CadastrarBeneficiarioRequest.class))).thenReturn(beneficiario);

            mockMvc.perform(
                            post("/api/v1/beneficiarios")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonStringHelper.asJsonString(request))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cns").value(beneficiario.getCns()))
                    .andExpect(jsonPath("$.nome").value(beneficiario.getNome()))
                    .andExpect(jsonPath("$.cpf").value(beneficiario.getCpf()));

            verify(service, times(1)).cadastrar(any(CadastrarBeneficiarioRequest.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarBeneficiario_CnsJaCadastrado() throws Exception {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest("012345678901234", "01234567890");

            when(service.cadastrar(any(CadastrarBeneficiarioRequest.class)))
                    .thenThrow(new BeneficiarioCadastradoException(BENEFICIARIO_JA_EXISTE));

            mockMvc.perform(
                            post("/api/v1/beneficiarios")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonStringHelper.asJsonString(request))
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.erro").value(BENEFICIARIO_JA_EXISTE))
                    .andExpect(jsonPath("$.rota").value("/api/v1/beneficiarios"));

            verify(service, times(1)).cadastrar(any(CadastrarBeneficiarioRequest.class));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarBeneficiario_CpfJaCadastrado() throws Exception {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest("012345678901234", "01234567890");

            when(service.cadastrar(any(CadastrarBeneficiarioRequest.class)))
                    .thenThrow(new BeneficiarioCadastradoException(BENEFICIARIO_JA_EXISTE));

            mockMvc.perform(
                            post("/api/v1/beneficiarios")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonStringHelper.asJsonString(request))
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.erro").value(BENEFICIARIO_JA_EXISTE))
                    .andExpect(jsonPath("$.rota").value("/api/v1/beneficiarios"));

            verify(service, times(1)).cadastrar(any(CadastrarBeneficiarioRequest.class));
        }
    }

    @Nested
    class BuscarBeneficiario {
        @Test
        void deveBuscarPorCns() throws Exception {
            String cns = "654987123065482";
            Beneficiario beneficiario = gerarBeneficiario();
            BeneficiarioResponse beneficiarioResponse = gerarBeneficiarioResponse();

            when(service.buscarPorCns(anyString())).thenReturn(beneficiario);

            mockMvc.perform(get("/api/v1/beneficiarios/{cns}", cns))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cns").value(cns))
                    .andExpect(jsonPath("$.nome").value(beneficiario.getNome()))
                    .andExpect(jsonPath("$.cpf").value(beneficiario.getCpf()));

            verify(service, times(1)).buscarPorCns(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPorCns_CnsNaoEncontrado() throws Exception {
            String cns = "654987123065482";

            when(service.buscarPorCns(anyString()))
                    .thenThrow(new BeneficiarioNaoEncontradoException(BENEFICIARIO_NAO_ENCONTRADO));

            mockMvc.perform(get("/api/v1/beneficiarios/{cns}", cns))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.erro").value(BENEFICIARIO_NAO_ENCONTRADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/beneficiarios/"+cns));

            verify(service, times(1)).buscarPorCns(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPorCns_AcessoNegadoADadosDeOutrosUsuarios() throws Exception {
            String cns = "654987123065482";

            when(service.buscarPorCns(anyString()))
                    .thenThrow(new UsuarioNaoIguaisException(ACESSO_NEGADO));

            mockMvc.perform(get("/api/v1/beneficiarios/{cns}", cns))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                    .andExpect(jsonPath("$.erro").value(ACESSO_NEGADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/beneficiarios/"+cns));

            verify(service, times(1)).buscarPorCns(anyString());
        }
    }

    @Nested
    class AtivarBeneficiario {
        @Test
        void deveAtivarBeneficiario() throws Exception {
            String cns = "654987123065482";
            Beneficiario beneficiario = gerarBeneficiario();
            BeneficiarioResponse beneficiarioResponse = gerarBeneficiarioResponse();

            when(service.ativar(anyString())).thenReturn(beneficiario);

            mockMvc.perform(patch("/api/v1/beneficiarios/{cns}", cns))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cns").value(cns))
                    .andExpect(jsonPath("$.nome").value(beneficiario.getNome()))
                    .andExpect(jsonPath("$.ativo").value(beneficiario.getAtivo()))
                    .andExpect(jsonPath("$.cpf").value(beneficiario.getCpf()));

            verify(service, times(1)).ativar(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoAtivarBeneficiario_CnsNaoEncontrado() throws Exception {
            String cns = "654987123065482";

            when(service.ativar(anyString()))
                    .thenThrow(new BeneficiarioNaoEncontradoException(BENEFICIARIO_NAO_ENCONTRADO));

            mockMvc.perform(patch("/api/v1/beneficiarios/{cns}", cns))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.erro").value(BENEFICIARIO_NAO_ENCONTRADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/beneficiarios/"+cns));

            verify(service, times(1)).ativar(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoAtivarBeneficiario_AcessoNegado() throws Exception {
            String cns = "654987123065482";

            when(service.ativar(anyString()))
                    .thenThrow(new UsuarioNaoIguaisException(ACESSO_NEGADO));

            mockMvc.perform(patch("/api/v1/beneficiarios/{cns}", cns))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                    .andExpect(jsonPath("$.erro").value(ACESSO_NEGADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/beneficiarios/"+cns));

            verify(service, times(1)).ativar(anyString());
        }
    }

    @Nested
    class DesativarBeneficiario {
        @Test
        void deveDesativarBeneficiario() throws Exception {
            String cns = "654987123065482";
            Beneficiario beneficiario = gerarBeneficiario(false);
            BeneficiarioResponse beneficiarioResponse = gerarBeneficiarioResponse(false);

            when(service.desativar(anyString())).thenReturn(beneficiario);

            mockMvc.perform(delete("/api/v1/beneficiarios/{cns}", cns))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cns").value(cns))
                    .andExpect(jsonPath("$.nome").value(beneficiario.getNome()))
                    .andExpect(jsonPath("$.ativo").value(beneficiario.getAtivo()))
                    .andExpect(jsonPath("$.cpf").value(beneficiario.getCpf()));

            verify(service, times(1)).desativar(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoDesativarBeneficiario_CnsNaoEncontrado() throws Exception {
            String cns = "654987123065482";

            when(service.desativar(anyString()))
                    .thenThrow(new BeneficiarioNaoEncontradoException(BENEFICIARIO_NAO_ENCONTRADO));

            mockMvc.perform(delete("/api/v1/beneficiarios/{cns}", cns))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.erro").value(BENEFICIARIO_NAO_ENCONTRADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/beneficiarios/"+cns));

            verify(service, times(1)).desativar(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoDesativarBeneficiario_AcessoNegado() throws Exception {
            String cns = "654987123065482";

            when(service.desativar(anyString()))
                    .thenThrow(new UsuarioNaoIguaisException(ACESSO_NEGADO));

            mockMvc.perform(delete("/api/v1/beneficiarios/{cns}", cns))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                    .andExpect(jsonPath("$.erro").value(ACESSO_NEGADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/beneficiarios/"+cns));

            verify(service, times(1)).desativar(anyString());
        }
    }
}
