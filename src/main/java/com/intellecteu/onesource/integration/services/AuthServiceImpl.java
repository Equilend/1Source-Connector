package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.AuthToken;
import com.intellecteu.onesource.integration.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.util.Http;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.intellecteu.onesource.integration.constant.AuthConstant.CLIENT_ID;
import static com.intellecteu.onesource.integration.constant.AuthConstant.CLIENT_SECRET;
import static com.intellecteu.onesource.integration.constant.AuthConstant.GRANT_TYPE;
import static com.intellecteu.onesource.integration.constant.AuthConstant.REALM_PATH;
import static com.intellecteu.onesource.integration.constant.AuthConstant.REFRESH_TOKEN;
import static com.intellecteu.onesource.integration.constant.AuthConstant.SECRET;
import static com.intellecteu.onesource.integration.constant.AuthConstant.TOKEN_PATH;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  @Value("${onesource.auth.username}")
  private String userName;
  @Value("${onesource.auth.password}")
  private String password;

  private final AuthzClient authClient;
  private final AuthMapper authMapper;

  private AuthToken authToken;

  @Override
  public AuthToken getAccessToken() {
    if (authToken == null) {
      return obtainAuthToken();
    }
    if (authToken.isAccessTokenExpired()) {
      log.debug("Access token {} is expired!",
          authToken.getAccessToken().substring(authToken.getAccessToken().length() - 20));
      if (authToken.isRefreshTokenExpired()) {
        return obtainAuthToken();
      }
      authToken = refreshToken(authToken.getRefreshToken());
    }
    return authToken;
  }

  private AuthToken obtainAuthToken() {
    log.debug("Obtaining a new access token from the authorization server.");
    var accessTokenResponse = authClient.obtainAccessToken(userName, password);
    authToken = authMapper.mapToAuthToken(accessTokenResponse);
    log.debug("A new access token {} is valid until {}",
        authToken.getAccessToken().substring(authToken.getAccessToken().length() - 20), authToken.getExpiresAt());
    return authToken;
  }

  @Override
  public AuthToken refreshToken(String refreshToken) {
    var keycloakConfig = authClient.getConfiguration();
    var authTokenUrl = keycloakConfig.getAuthServerUrl() + REALM_PATH + keycloakConfig.getRealm() + TOKEN_PATH;
    log.debug("Obtaining a new access token with refresh token from: {}.", authTokenUrl);
    var clientId = keycloakConfig.getResource();
    var credentials = keycloakConfig.getCredentials();
    String secret = "";
    if (credentials != null) {
      if (credentials.containsKey(SECRET)) {
        secret = String.valueOf(credentials.get(SECRET));
      }
    }

    var http = new Http(keycloakConfig, (params, headers) -> {});

    var tokenResponse = http.<AccessTokenResponse>post(authTokenUrl)
        .authentication()
        .client()
        .form()
          .param(GRANT_TYPE, REFRESH_TOKEN)
          .param(REFRESH_TOKEN, refreshToken)
          .param(CLIENT_ID, clientId)
          .param(CLIENT_SECRET, secret)
        .response()
        .json(AccessTokenResponse.class)
        .execute();
    authToken = authMapper.mapToAuthToken(tokenResponse);
    log.debug("A new access token {} is valid until {}",
        authToken.getAccessToken().substring(authToken.getAccessToken().length() - 20), authToken.getExpiresAt());
    return authToken;
  }
}
