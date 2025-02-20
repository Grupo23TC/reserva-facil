package br.com.fiap.hackathon.reservafacil.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Library API",
                version = "v1",
                contact = @Contact(
                        name = "Lucas Franco",
                        email = "lucas@email.com",
                        url = "libraryapi.com"
                )
        ),
        // Indica que temos um requerimento de segurança
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
// Vamos definir como vai funcionar o bearerAuth
@SecurityScheme(
        name = "bearerAuth",
        // Definimos como http pois vamos colocar o token e ele vai fazer uma requisição http
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfiguration {
}