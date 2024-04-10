package com.intellecteu.onesource.integration.config.security;

import java.util.ArrayList;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public class OneSourceJwtClaimsConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter converter;
    private final String uiClientId;

    public OneSourceJwtClaimsConverter(String uiClientId, JwtGrantedAuthoritiesConverter converter) {
        this.uiClientId = uiClientId;
        this.converter = converter;
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        final String clientIdJwtCode = "azp";
        var grantedAuth = new ArrayList<>(converter.convert(jwt));
        var clientRole = (String) jwt.getClaims().get(clientIdJwtCode);
        if (clientRole != null && clientRole.equalsIgnoreCase(uiClientId)) {
            grantedAuth.add(new SimpleGrantedAuthority("ROLE_" + clientRole));
        }
        return new JwtAuthenticationToken(jwt, grantedAuth);
    }
}
