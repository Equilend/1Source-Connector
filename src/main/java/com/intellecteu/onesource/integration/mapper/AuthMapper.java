package com.intellecteu.onesource.integration.mapper;

import com.auth0.jwt.JWT;
import com.intellecteu.onesource.integration.dto.AuthToken;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
public class AuthMapper {

  public AuthToken mapToAuthToken(AccessTokenResponse tokenResponse) {
    return AuthToken.builder()
        .accessToken(tokenResponse.getToken())
        .expiresIn(tokenResponse.getExpiresIn())
        .expiresAt(convertToDateTime(JWT.decode(tokenResponse.getToken()).getExpiresAt()))
        .refreshToken(tokenResponse.getRefreshToken())
        .refreshExpiresIn(tokenResponse.getRefreshExpiresIn())
        .refreshExpiresAt(convertToDateTime(JWT.decode(tokenResponse.getRefreshToken()).getExpiresAt()))
        .tokenType(tokenResponse.getTokenType())
        .build();
  }

  private LocalDateTime convertToDateTime(Date date) {
    return date.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
  }
}
