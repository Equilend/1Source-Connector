package com.intellecteu.onesource.integration.config.security;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfiguration;

@Slf4j
@Configuration
@EnableWebSecurity
public class OneSourceSecurityConfig {

    private final String uiClientId;

    public OneSourceSecurityConfig(@Value("${onesource.auth.api.resource}") String uiClientId) {
        this.uiClientId = uiClientId;
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    @Profile("!local & !test")
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
            auth
                .anyRequest()
                .hasRole(uiClientId));

        http.csrf(Customizer.withDefaults());
        // todo Remove or Configure according to the requirements after the demo
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration corsConfig = new CorsConfiguration();
            corsConfig.setAllowedOrigins(List.of("http://localhost:3000"));
            corsConfig.setAllowedMethods(List.of("*"));
            corsConfig.setAllowedHeaders(List.of("*"));
            return corsConfig;
        }));

        http.oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(
                new OneSourceJwtClaimsConverter(uiClientId, new JwtGrantedAuthoritiesConverter()))));
        return http.build();
    }

    @Bean
    @Profile({"local", "test"})
    public SecurityFilterChain resourceServerFilterChainLocal(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
            auth
                .anyRequest()
                .permitAll());

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration corsConfig = new CorsConfiguration();
            corsConfig.setAllowedOrigins(List.of("http://localhost:3000"));
            corsConfig.setAllowedMethods(List.of("*"));
            corsConfig.setAllowedHeaders(List.of("*"));
            return corsConfig;
        }));

        return http.build();
    }
}
