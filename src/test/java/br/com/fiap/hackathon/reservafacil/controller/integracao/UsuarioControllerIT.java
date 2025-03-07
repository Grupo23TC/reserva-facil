package br.com.fiap.hackathon.reservafacil.controller.integracao;

import br.com.fiap.hackathon.reservafacil.model.dto.usuario.AtualizarSenhaRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.usuario.CadastrarUsuarioRequest;
import br.com.fiap.hackathon.reservafacil.helper.OAuthHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static br.com.fiap.hackathon.reservafacil.util.UsuarioUtil.gerarAtualizarSenhaRequest;
import static br.com.fiap.hackathon.reservafacil.util.UsuarioUtil.gerarCadastrarUsuarioRequest;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerIT {
    private String token = "";

    @Value("${credenciais.client_id}")
    private String clientId;

    @Value("${credenciais.password}")
    private String senha;
    @Value("${credenciais.redirect_uri}")
    private String redirectUri;
    @Value("${credenciais.auth}")
    private String auth;

    @LocalServerPort
    int port;

    @BeforeAll
    void beforeAll() throws Exception {
        // Aqui nao foi usado o usuario padrao para nao interferir nos demais testes
        String cns = "987654321098765";

        String code = new OAuthHelper().getOAuthToken(cns, senha, clientId, redirectUri, String.valueOf(port));

        token = given()
                .header("Authorization", "Basic " + auth)
                .contentType(ContentType.URLENC)
                .formParam("code", code)
                .formParam("grant_type", "authorization_code")
                .formParam("redirect_uri", redirectUri)
                .when()
                .post(String.format("http://localhost:%s/oauth2/token", port))
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getString("access_token");
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class CadastraUsuario {
        @Test
        void deveCadastrarUsuario() {
            CadastrarUsuarioRequest request = gerarCadastrarUsuarioRequest("985320456712560");

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post("/api/v1/usuarios")
            .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath("schema/usuario/usuarioResponse.schema.json"));

        }

        @Test
        void deveGerarExcecao_QuandoCadastrarUsuario_CnsJaCadastrado() {
            CadastrarUsuarioRequest request = gerarCadastrarUsuarioRequest("123456789012345");

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post("/api/v1/usuarios")
            .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarUsuario_RoleNaoEncontrada() {
            CadastrarUsuarioRequest request = gerarCadastrarUsuarioRequest("235874159680254", "PACINTES");

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/api/v1/usuarios")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }
    }

    @Nested
    class AtualizarUsuario {
        @Test
        void deveAtualizarUsuario() {
            String cns = "987654321098765";
            AtualizarSenhaRequest request = gerarAtualizarSenhaRequest();

            given()
                    .auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .patch("/api/v1/usuarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

        }

        @Test
        void deveGerarExcecao_QuandoAtualizarUsuario_CnsNaoEncontrado() {
            String cns = "123456789012348";
            AtualizarSenhaRequest request = gerarAtualizarSenhaRequest();

            given()
                    .auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .patch("/api/v1/usuarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));

        }

        @Test
        void deveGerarExcecao_QuandoAtualizarSenha_AcessoNegado() {
            String cns = "123456789012345";
            AtualizarSenhaRequest request = gerarAtualizarSenhaRequest();

            given()
                    .auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .patch("/api/v1/usuarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }
    }

    @Nested
    class AtivarUsuario {
        @Test
        void deveAtivarUsuario() {
            String cns = "987654321098765";

            given()
                    .auth().oauth2(token)
            .when()
                    .put("/api/v1/usuarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schema/usuario/usuarioResponse.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarUsuario_CnsNaoEncontrado() {
            String cns = "987654321098768";

            given()
                    .auth().oauth2(token)
            .when()
                    .put("/api/v1/usuarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarUsuario_AcessoNegado() {
            String cns = "123456789012345";

            given()
                    .auth().oauth2(token)
            .when()
                    .put("/api/v1/usuarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }
    }

    @Nested
    class InativarUsuario {
        @Test
        void deveAtivarUsuario() {
            String cns = "987654321098765";

            given()
                    .auth().oauth2(token)
            .when()
                    .delete("/api/v1/usuarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schema/usuario/usuarioResponse.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarUsuario_CnsNaoEncontrado() {
            String cns = "987654321098768";

            given()
                    .auth().oauth2(token)
            .when()
                    .delete("/api/v1/usuarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarUsuario_AcessoNegado() {
            String cns = "123456789012345";

            given()
                    .auth().oauth2(token)
            .when()
                    .delete("/api/v1/usuarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }
    }
}
