package com.intellecteu.onesource.integration.config;

import static com.intellecteu.onesource.integration.constant.AuthConstant.SECRET;

import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.authorization.client.AuthzClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AuthConfig {

    @Value("${onesource.auth.server-url}")
    private String authServerUrl;

    @Value("${onesource.auth.realm}")
    private String realm;

    @Value("${onesource.auth.client-id}")
    private String clientId;

    @Value("${onesource.auth.credentials.secret}")
    private String secret;

    /**
     * A client for retrieving access token from the Keycloak server.
     *
     * @return AuthzClient
     */
    @Bean
    public AuthzClient authClient() {
        return AuthzClient.create(config());
    }

    private org.keycloak.authorization.client.Configuration config() {
        var credentials = new HashMap<String, Object>();
        credentials.put(SECRET, secret);
        log.debug("Configuring AuthzClient with url:{}, realm:{}, clientId:{}, secret:{}",
            authServerUrl, realm, clientId, secret);
        return new org.keycloak.authorization.client.Configuration(
            authServerUrl,
            realm,
            clientId,
            credentials,
            null
        );
    }

}
