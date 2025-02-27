package br.com.fiap.hackathon.reservafacil.controller;

import br.com.fiap.hackathon.reservafacil.util.JsonStringHelper;
import br.com.fiap.hackathon.reservafacil.exception.handler.GlobalExceptionHandler;
import br.com.fiap.hackathon.reservafacil.exception.role.RoleNaoEncontradaException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioCadastradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioNaoEncontradoException;
import br.com.fiap.hackathon.reservafacil.exception.usuario.UsuarioNaoIguaisException;
import br.com.fiap.hackathon.reservafacil.model.Usuario;
import br.com.fiap.hackathon.reservafacil.model.dto.usuario.AtualizarSenhaRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.usuario.CadastrarUsuarioRequest;
import br.com.fiap.hackathon.reservafacil.service.impl.UsuarioServiceImpl;
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

import static br.com.fiap.hackathon.reservafacil.util.UsuarilUtil.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UsuarioControllerTest {
    private static final String USUARIO_JA_CADASTRADO = "CNS já cadastrado.";
    private static final String ROLE_NAO_ENCONTRADA = "Não foi possível encontrar a role.";
    private static final String NAO_FOI_POSSIVEL_ALTERAR_A_SENHA = "Não foi possível alterar a senha.";
    private static final String NAO_FOI_POSSIVEL_ATIVAR_USUARIO = "Não foi possível ativar usuário.";
    private static final String NAO_FOI_POSSIVEL_DESATIVAR_USUARIO = "Não foi possível desativar usuário.";

    private MockMvc mockMvc;

    @Mock
    private UsuarioServiceImpl service;

    @InjectMocks
    private UsuarioController controller;

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
    class CadastrarUsuario {
        @Test
        void deveCadastrarUsuario() throws Exception {
            CadastrarUsuarioRequest cadastrarUsuarioRequest = gerarCadastrarUsuarioRequest();
            Usuario usuario = gerarUsuario();

            when(service.salvarUsuario(anyString(), anyString(), anyString()))
                    .thenReturn(usuario);

            mockMvc.perform(
                    post("/api/v1/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonStringHelper.asJsonString(cadastrarUsuarioRequest))
            )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.cns").value(usuario.getCns()))
                    .andExpect(jsonPath("$.ativo").value(true))
                    .andExpect(jsonPath("$.roles").isArray());

            verify(service, times(1)).salvarUsuario(anyString(), anyString(), anyString());
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarUsuario_CnsJaCadastrado() throws Exception {
            CadastrarUsuarioRequest cadastrarUsuarioRequest = gerarCadastrarUsuarioRequest();

            when(service.salvarUsuario(anyString(), anyString(), anyString()))
                    .thenThrow(new UsuarioCadastradoException(USUARIO_JA_CADASTRADO));

            mockMvc.perform(
                    post("/api/v1/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonStringHelper.asJsonString(cadastrarUsuarioRequest))
            )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.erro").value(USUARIO_JA_CADASTRADO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/usuarios"));

            verify(service, times(1)).salvarUsuario(anyString(), anyString(), anyString());
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarRole_RoleNaoEncontrada() throws Exception {
            CadastrarUsuarioRequest cadastrarUsuarioRequest = gerarCadastrarUsuarioRequest();

            when(service.salvarUsuario(anyString(), anyString(), anyString()))
                    .thenThrow(new RoleNaoEncontradaException(ROLE_NAO_ENCONTRADA));

            mockMvc.perform(
                            post("/api/v1/usuarios")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonStringHelper.asJsonString(cadastrarUsuarioRequest))
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.erro").value(ROLE_NAO_ENCONTRADA))
                    .andExpect(jsonPath("$.rota").value("/api/v1/usuarios"));
        }
    }

    @Nested
    class AtualizarUsuario {
        @Test
        void deveAtualizarSenha() throws Exception {
            String cns = "654987123065482";
            AtualizarSenhaRequest request = gerarAtualizarSenhaRequest();


            doNothing().when(service).atualizarSenhaUsuario(anyString(), anyString());

            mockMvc.perform(
                    patch("/api/v1/usuarios/{cns}", cns)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonStringHelper.asJsonString(request))
            )
                    .andExpect(status().isNoContent());

            verify(service, times(1)).atualizarSenhaUsuario(anyString(), anyString());
        }

        @Test
        void deveGerarExcecao_QuandoAtualizarSenha_CnsNaoEncontrado() throws Exception {
            String cns = "654987123065482";
            AtualizarSenhaRequest request = gerarAtualizarSenhaRequest();


            doThrow(new UsuarioNaoEncontradoException(NAO_FOI_POSSIVEL_ALTERAR_A_SENHA))
                    .when(service).atualizarSenhaUsuario(anyString(), anyString());

            mockMvc.perform(
                            patch("/api/v1/usuarios/{cns}", cns)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonStringHelper.asJsonString(request))
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.erro").value(NAO_FOI_POSSIVEL_ALTERAR_A_SENHA))
                    .andExpect(jsonPath("$.rota").value("/api/v1/usuarios/"+cns));

            verify(service, times(1)).atualizarSenhaUsuario(anyString(), anyString());
        }

        @Test
        void deveGerarExcecao_QuandoAtualizarSenha_AcessoNegado() throws Exception {
            String cns = "654987123065482";
            AtualizarSenhaRequest request = gerarAtualizarSenhaRequest();


            doThrow(new UsuarioNaoIguaisException(NAO_FOI_POSSIVEL_ALTERAR_A_SENHA))
                    .when(service).atualizarSenhaUsuario(anyString(), anyString());

            mockMvc.perform(
                            patch("/api/v1/usuarios/{cns}", cns)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(JsonStringHelper.asJsonString(request))
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                    .andExpect(jsonPath("$.erro").value(NAO_FOI_POSSIVEL_ALTERAR_A_SENHA))
                    .andExpect(jsonPath("$.rota").value("/api/v1/usuarios/"+cns));

            verify(service, times(1)).atualizarSenhaUsuario(anyString(), anyString());
        }
    }

    @Nested
    class AtivarUsuario {
        @Test
        void deveAtivarUsuario() throws Exception{
            String cns = "654987123065482";
            Usuario usuario = gerarUsuario(true);

            when(service.ativar(anyString())).thenReturn(usuario);

            mockMvc.perform(put("/api/v1/usuarios/{cns}", cns))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cns").value(cns))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.ativo").value(true));

            verify(service, times(1)).ativar(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoAtivarUsuario_CnsNaoEncontrado() throws Exception{
            String cns = "654987123065482";

            when(service.ativar(anyString())).thenThrow(new UsuarioNaoEncontradoException(NAO_FOI_POSSIVEL_ATIVAR_USUARIO));

            mockMvc.perform(put("/api/v1/usuarios/{cns}", cns))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.erro").value(NAO_FOI_POSSIVEL_ATIVAR_USUARIO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/usuarios/"+cns));;

            verify(service, times(1)).ativar(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoAtivarUsuario_AcessoNegado() throws Exception{
            String cns = "654987123065482";

            when(service.ativar(anyString())).thenThrow(new UsuarioNaoIguaisException(NAO_FOI_POSSIVEL_ATIVAR_USUARIO));

            mockMvc.perform(put("/api/v1/usuarios/{cns}", cns))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                    .andExpect(jsonPath("$.erro").value(NAO_FOI_POSSIVEL_ATIVAR_USUARIO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/usuarios/"+cns));;

            verify(service, times(1)).ativar(anyString());
        }
    }

    @Nested
    class DesativarUsuario {
        @Test
        void deveDesativarUsuario() throws Exception{
            String cns = "654987123065482";
            Usuario usuario = gerarUsuario(false);

            when(service.desativar(anyString())).thenReturn(usuario);

            mockMvc.perform(delete("/api/v1/usuarios/{cns}", cns))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cns").value(cns))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.ativo").value(false));

            verify(service, times(1)).desativar(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoDesativarUsuario_CnsNaoEncontrado() throws Exception{
            String cns = "654987123065482";

            when(service.desativar(anyString())).thenThrow(new UsuarioNaoEncontradoException(NAO_FOI_POSSIVEL_DESATIVAR_USUARIO));

            mockMvc.perform(delete("/api/v1/usuarios/{cns}", cns))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.erro").value(NAO_FOI_POSSIVEL_DESATIVAR_USUARIO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/usuarios/"+cns));;

            verify(service, times(1)).desativar(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoDesativarUsuario_AcessoNegado() throws Exception{
            String cns = "654987123065482";

            when(service.desativar(anyString())).thenThrow(new UsuarioNaoIguaisException(NAO_FOI_POSSIVEL_DESATIVAR_USUARIO));

            mockMvc.perform(delete("/api/v1/usuarios/{cns}", cns))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.horario").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                    .andExpect(jsonPath("$.erro").value(NAO_FOI_POSSIVEL_DESATIVAR_USUARIO))
                    .andExpect(jsonPath("$.rota").value("/api/v1/usuarios/"+cns));;

            verify(service, times(1)).desativar(anyString());
        }
    }
}
