package com.intellecteu.onesource.integration.controller;

import com.intellecteu.onesource.integration.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller is only for development test purposes.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthTestController {

  private final AuthService authService;

  /**
   * Test token retrieving from an auth server.
   *
   * @return String JWT access token.
   */
  @GetMapping("/token")
  public String getAccessToken() {
    var token = authService.getAccessToken();

    log.info("Access token: " + token.getAccessToken());
    log.info("Refresh token: " + token.getRefreshToken());
    log.info("Expires in {} at {}", token.getExpiresIn(), token.getExpiresAt());
    log.info("Refresh token expires in {} at {}", token.getRefreshExpiresIn(), token.getRefreshExpiresAt());
    log.info("Token type: " + token.getTokenType());

    return token.getAccessToken();
  }

}
