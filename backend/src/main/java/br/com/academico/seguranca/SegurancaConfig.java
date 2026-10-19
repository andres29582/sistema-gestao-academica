package br.com.academico.seguranca;

import br.com.academico.erro.TratadorErros;
import br.com.academico.repositorio.UsuarioRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import java.security.SecureRandom;
import java.util.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

@Configuration
public class SegurancaConfig {
    @Bean PasswordEncoder senhas() { return new BCryptPasswordEncoder(12); }
    @Bean SecretKey chave(@Value("${app.jwt-segredo:}") String segredo) {
        byte[] bytes;
        if (segredo.isBlank()) { bytes = new byte[32]; new SecureRandom().nextBytes(bytes); }
        else {
            bytes = Base64.getDecoder().decode(segredo);
            if (bytes.length < 32) throw new IllegalArgumentException("APP_JWT_SEGREDO deve ter ao menos 32 bytes em Base64.");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
    @Bean JwtEncoder codificador(SecretKey chave) { return new NimbusJwtEncoder(new ImmutableSecret<>(chave)); }
    @Bean JwtDecoder decodificador(SecretKey chave) {
        var decoder = NimbusJwtDecoder.withSecretKey(chave).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("academico-beta"));
        return decoder;
    }
    @Bean SecurityFilterChain filtros(HttpSecurity http, UsuarioRepositorio usuarios, ObjectMapper json,
            @Value("${app.origens-permitidas}") String origens) throws Exception {
        var cors = new CorsConfiguration();
        cors.setAllowedOrigins(Arrays.asList(origens.split(",")));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        var fonteCors = new UrlBasedCorsConfigurationSource(); fonteCors.registerCorsConfiguration("/**", cors);
        http.csrf(c -> c.disable()).cors(c -> c.configurationSource(fonteCors))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                    .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/cursos", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                    .requestMatchers("/api/professores/me/**").hasRole("PROFESSOR")
                    .requestMatchers("/api/admin/**", "/api/alunos/**", "/api/professores/**", "/api/matriculas/**").hasRole("ADMIN")
                    .requestMatchers("/api/me/**", "/api/me").authenticated()
                    .anyRequest().denyAll())
                .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(jwt -> {
                    Long id;
                    try { id = Long.valueOf(jwt.getSubject()); }
                    catch (NumberFormatException e) { throw new OAuth2AuthenticationException("invalid_token"); }
                    var usuario = usuarios.findById(id).filter(u -> u.habilitado())
                            .orElseThrow(() -> new OAuth2AuthenticationException("invalid_token"));
                    return UsernamePasswordAuthenticationToken.authenticated(new ContaAutenticada(id, usuario.getPapel()), null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPapel().name())));
                })).authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401); res.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                    res.setHeader("WWW-Authenticate", "Bearer");
                    json.writeValue(res.getOutputStream(), TratadorErros.problema(HttpStatus.UNAUTHORIZED, "AUTENTICACAO_NECESSARIA", "Autentique-se com uma conta habilitada."));
                }))
                .exceptionHandling(e -> e
                    .authenticationEntryPoint((req, res, ex) -> {
                        res.setStatus(401); res.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                        res.setHeader("WWW-Authenticate", "Bearer");
                        json.writeValue(res.getOutputStream(), TratadorErros.problema(HttpStatus.UNAUTHORIZED, "AUTENTICACAO_NECESSARIA", "Autentique-se com uma conta habilitada."));
                    })
                    .accessDeniedHandler((req, res, ex) -> {
                        res.setStatus(403); res.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                        json.writeValue(res.getOutputStream(), TratadorErros.problema(HttpStatus.FORBIDDEN, "ACESSO_NEGADO", "Você não tem permissão para esta operação."));
                    }));
        return http.build();
    }
    @Bean OpenAPI documentacao() {
        return new OpenAPI().info(new Info().title("Gestão Acadêmica — Beta 1.0").version("1.0.0-beta.1")
                .description("Demonstração com dados fictícios e H2 volátil. Não utilizar dados pessoais reais."))
                .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
