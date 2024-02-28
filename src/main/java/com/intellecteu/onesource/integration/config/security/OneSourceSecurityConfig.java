package com.intellecteu.onesource.integration.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Slf4j
@Configuration
@EnableWebSecurity
public class OneSourceSecurityConfig {

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
                .authenticated());

        http.csrf(Customizer.withDefaults());
        http.cors(Customizer.withDefaults());

        http.oauth2ResourceServer((oauth2) -> oauth2
            .jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    @Profile({"local", "test"})
    public SecurityFilterChain resourceServerFilterChainLocal(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
            auth
                .anyRequest()
                .permitAll());

        http.csrf(Customizer.withDefaults());
        http.cors(Customizer.withDefaults());

        http.oauth2ResourceServer((oauth2) -> oauth2
            .jwt(Customizer.withDefaults()));
        return http.build();
    }
}
