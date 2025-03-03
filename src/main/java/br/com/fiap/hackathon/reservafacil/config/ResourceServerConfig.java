package br.com.fiap.hackathon.reservafacil.config;

import br.com.fiap.hackathon.reservafacil.security.JwtCustomAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class ResourceServerConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtCustomAuthenticationFilter filter
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                // Adiciona a página de login customizada
                .formLogin(configurer -> configurer.loginPage("/login").permitAll())
                // Estamos falando que todas requisições devem ser autenticadas.
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests.requestMatchers("/login").permitAll();
                    authorizeRequests.requestMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll();
                    authorizeRequests.requestMatchers(HttpMethod.POST, "/api/v1/beneficiarios").permitAll();
                    authorizeRequests.requestMatchers(HttpMethod.POST, "/api/v1/prestadores").permitAll();
                    authorizeRequests.requestMatchers(HttpMethod.POST, "/api/v1/operadores").permitAll();

                    authorizeRequests.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2Rs -> oauth2Rs.jwt(Customizer.withDefaults()))
                // estamos falando que o filtro criado por nós deve ser executado depois do filtro de BearerToken
                .addFilterAfter(filter, BearerTokenAuthenticationFilter.class)
                .build();
    }

    // Configura no token JWT o prefixo de SCOPE
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var auhtoritiesConverter = new JwtGrantedAuthoritiesConverter();
        auhtoritiesConverter.setAuthorityPrefix("");

        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(auhtoritiesConverter);

        return converter;
    }

    // Configura o prefixo ROLE
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/v2/api-docs/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/webjars/**",
                "/h2-console/**"
        );
    }
}
