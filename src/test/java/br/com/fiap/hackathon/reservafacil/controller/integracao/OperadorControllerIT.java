package br.com.fiap.hackathon.reservafacil.controller.integracao;

import br.com.fiap.hackathon.reservafacil.helper.OAuthHelper;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import br.com.fiap.hackathon.reservafacil.model.dto.operador.CadastrarOperadorRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static br.com.fiap.hackathon.reservafacil.util.BeneficiarioUtil.gerarCadastrarBeneficiarioRequest;
import static br.com.fiap.hackathon.reservafacil.util.OperadorUtil.gerarCadastrarOperadorRequest;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OperadorControllerIT {
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
        String cns = "234567890123456";

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
    class CadastrarOperador {
        @Test
        void deveCadastrarOperador() {
            CadastrarOperadorRequest request = gerarCadastrarOperadorRequest("875369568201458", UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post("/api/v1/operadores")
            .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath("schema/operador/operadorResponse.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarOperador_CnsJaCadastrado() {
            CadastrarOperadorRequest request = gerarCadastrarOperadorRequest("234567890123456", UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/api/v1/operadores")
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }
    }

    @Nested
    class BuscarOperador {
        @Test
        void deveBuscarOperador() {
            String cns = "234567890123456";
            
            given()
                    .auth().oauth2(token)
            .when()
                    .get("/api/v1/operadores/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schema/operador/operadorResponse.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarOperador_CnsNaoEncontrado() {
            String cns = "234567890123458";

            given()
                    .auth().oauth2(token)
            .when()
                    .get("/api/v1/operadores/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }

        @Test
        void deveGerarExcecap_QuandoBuscarOperador_AcessoNegado() {
            String cns = "876543210987654";

            given()
                    .auth().oauth2(token)
            .when()
                    .get("/api/v1/operadores/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }
    }

    @Nested
    class AtivarOperador {
        @Test
        void deveAtivarOperador() {
            String cns = "234567890123456";

            given()
                    .auth().oauth2(token)
            .when()
                    .patch("/api/v1/operadores/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schema/operador/operadorResponse.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarOperador_CnsNaoEncontrado() {
            String cns = "234567890123458";

            given()
                    .auth().oauth2(token)
            .when()
                    .patch("/api/v1/operadores/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarOperador_AcessoNegado() {
            String cns = "876543210987654";

            given()
                    .auth().oauth2(token)
            .when()
                    .patch("/api/v1/operadores/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }
    }

    @Nested
    class InativarOperador {
        @Test
        void deveInativarOperador() {
            String cns = "234567890123456";

            given()
                    .auth().oauth2(token)
            .when()
                    .delete("/api/v1/operadores/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schema/operador/operadorResponse.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoInativarOperador_CnsNaoEncontrado() {
            String cns = "234567890123458";

            given()
                    .auth().oauth2(token)
            .when()
                    .patch("/api/v1/operadores/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoInativarOperador_AcessoNegado() {
            String cns = "876543210987654";

            given()
                    .auth().oauth2(token)
            .when()
                    .delete("/api/v1/operadores/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }
    }
}
