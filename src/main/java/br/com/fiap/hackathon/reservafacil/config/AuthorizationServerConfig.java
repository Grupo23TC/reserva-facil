package br.com.fiap.hackathon.reservafacil.config;

import br.com.fiap.hackathon.reservafacil.model.Role;
import br.com.fiap.hackathon.reservafacil.security.CustomAuthentication;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {
    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey;

    @Value("${jwt.private.key}")
    private RSAPrivateKey privateKey;

    @Value("${jwt.key_id}")
    private String keyId;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer configurer = OAuth2AuthorizationServerConfigurer.authorizationServer();

        http
                .securityMatcher(configurer.getEndpointsMatcher())
                .with(configurer, authorizationServer ->
                        authorizationServer.oidc(Customizer.withDefaults())
                )
                .oauth2ResourceServer(oauth2Rs -> oauth2Rs.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage("/login"))
                .exceptionHandling(exception -> exception.
                        defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenSettings tokenSettings() {
        return TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .accessTokenTimeToLive(Duration.ofMinutes(60))
                .refreshTokenTimeToLive(Duration.ofMinutes(120))
                .build();
    }

    @Bean
    public ClientSettings clientSettings() {
        return ClientSettings.builder()
                // O valor foi definido como false pois não vamos trabalhar com uma tela de consentimento
                .requireAuthorizationConsent(false)
                .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        RSAKey key = gerarChaveRSA();
        JWKSet jwkSet = new JWKSet(key);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private RSAKey gerarChaveRSA() {
        RSAPublicKey chavePublica = publicKey;
        RSAPrivateKey chavePrivada = privateKey;

        return new RSAKey
                .Builder(chavePublica)
                .privateKey(chavePrivada)
                .keyID(keyId)
                .algorithm(new Algorithm("RS256"))
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                // endpoint para obter token
                .tokenEndpoint("/oauth2/token")
                // para consultar status do token
                .tokenIntrospectionEndpoint("/oauth2/introspect")
                // Revogar token
                .tokenRevocationEndpoint("oauth2/revoke")
                // Utilizado para pegar o authorization code
                .authorizationEndpoint("/oauth2/authorize")
                // informações do usuário OpenID Connect
                .oidcUserInfoEndpoint("/oauth2/userinfo")
                //obter a chave publica para verificar a assinatura do token
                .jwkSetEndpoint("/oauth2/jwks")
                // logout
                .oidcLogoutEndpoint("/oauth2/logout")
                .build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            var principal = context.getPrincipal();

            if(!(principal instanceof CustomAuthentication authentication)) return;

            OAuth2TokenType tipoToken = context.getTokenType();

            if(!OAuth2TokenType.ACCESS_TOKEN.equals(tipoToken)) return;

            Collection<Role> authorities = authentication.getAuthorities();
            List<String> authoritiesList = authorities.stream().map(GrantedAuthority::getAuthority).toList();

            context
                    .getClaims()
                    .claim("authorities", authoritiesList);
        };
    }
}
