package br.com.fiap.hackathon.reservafacil.controller.integracao;

import br.com.fiap.hackathon.reservafacil.helper.OAuthHelper;
import br.com.fiap.hackathon.reservafacil.model.dto.beneficiario.CadastrarBeneficiarioRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static br.com.fiap.hackathon.reservafacil.util.BeneficiarioUtil.gerarCadastrarBeneficiarioRequest;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BeneficiarioControllerIT {
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
    class CadastrarBeneficiario {
        @Test
        void deveCadastrarBeneficiario() {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest("953214658201472", "43276125021");

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post("/api/v1/beneficiarios")
            .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath("schema/beneficiario/beneficiarioResponse.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarBeneficiario_CnsJaCadastrado() {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest("987654321098765", "18374799099");

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/api/v1/beneficiarios")
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarBeneficiario_CpfJaCadastrado() {
            CadastrarBeneficiarioRequest request = gerarCadastrarBeneficiarioRequest("568243105987526", "01234567890");

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                    .post("/api/v1/beneficiarios")
            .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }
    }

    @Nested
    class BuscarBeneficiario {
        @Test
        void deveBuscarBeneficiario() {
            String cns = "987654321098765";
            
            given()
                    .auth().oauth2(token)
            .when()
                    .get("/api/v1/beneficiarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schema/beneficiario/beneficiarioResponse.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarBeneficiario_CnsNaoEncontrado() {
            String cns = "987654321098768";

            given()
                    .auth().oauth2(token)
            .when()
                    .get("/api/v1/beneficiarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }

        @Test
        void deveGerarExcecap_QuandoBuscarBeneficiario_AcessoNegado() {
            String cns = "123456789012345";

            given()
                    .auth().oauth2(token)
            .when()
                    .get("/api/v1/beneficiarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }
    }

    @Nested
    class AtivarBeneficiario {
        @Test
        void deveAtivarBeneficiario() {
            String cns = "987654321098765";

            given()
                    .auth().oauth2(token)
            .when()
                    .patch("/api/v1/beneficiarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schema/beneficiario/beneficiarioResponse.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarBeneficiario_CnsNaoEncontrado() {
            String cns = "987654321098768";

            given()
                    .auth().oauth2(token)
            .when()
                    .patch("/api/v1/beneficiarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAtivarBeneficiario_AcessoNegado() {
            String cns = "123456789012345";

            given()
                    .auth().oauth2(token)
            .when()
                    .patch("/api/v1/beneficiarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }
    }

    @Nested
    class InativarBeneficiario {
        @Test
        void deveInativarBeneficiario() {
            String cns = "987654321098765";

            given()
                    .auth().oauth2(token)
            .when()
                    .delete("/api/v1/beneficiarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schema/beneficiario/beneficiarioResponse.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoInativarBeneficiario_CnsNaoEncontrado() {
            String cns = "987654321098768";

            given()
                    .auth().oauth2(token)
            .when()
                    .patch("/api/v1/beneficiarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoInativarBeneficiario_AcessoNegado() {
            String cns = "123456789012345";

            given()
                    .auth().oauth2(token)
            .when()
                    .delete("/api/v1/beneficiarios/{cns}", cns)
            .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .body(matchesJsonSchemaInClasspath("schema/excecao/excecaoGlobalExceptionHandler.schema.json"));
        }
    }
}
