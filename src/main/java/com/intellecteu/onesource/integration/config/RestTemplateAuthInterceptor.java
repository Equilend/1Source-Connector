package com.intellecteu.onesource.integration.config;

import com.intellecteu.onesource.integration.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestTemplateAuthInterceptor implements ClientHttpRequestInterceptor {

  private final AuthService authService;

  @Value("${spire.username}")
  private String spireUsername;

  @Value("${spire.password}")
  private String spirePassword;

  @Value("${onesource.baseEndpoint}")
  private String onesourceEndpoint;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {
    var uri = request.getURI().toString();
    if (uri.contains(onesourceEndpoint)) {
      var authToken = authService.getAccessToken();
      final String accessToken = authToken.getAccessToken();
      log.debug("Intercept HTTP request, adding token: " + accessToken.substring(accessToken.length() - 20));
      log.debug("Executing request to {}", uri);
      request.getHeaders().setBearerAuth(accessToken);
    } else {
      log.debug("Intercept the HTTP request, adding header Client_id.");
      log.debug("Executing request to {}", uri);
      final HttpHeaders headers = request.getHeaders();
      headers.add("Client_id", spireUsername);
    }

    return execution.execute(request, body);
  }
}
