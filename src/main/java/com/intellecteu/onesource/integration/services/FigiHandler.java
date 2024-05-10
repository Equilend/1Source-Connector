package com.intellecteu.onesource.integration.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class FigiHandler {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Extract figi code from ticker
     * Example ticker to figi code: AAPL -> BBG000B9XRY4
     *
     * @param ticker String ticer
     * @return String figi code or null
     */
    public String getFigiFromTicker(@NonNull String ticker) {
        // todo extract constants to the app configuration file.
        String requestBody = String.format("""
            [
                {
                    "idType":"TICKER",
                    "idValue":"%s",
                    "exchCode":"US",
                    "securityType": "Common Stock"
                }
            ]
            """, ticker);
        final URI requestUri = UriComponentsBuilder.fromHttpUrl("https://api.openfigi.com/v3/mapping").build().toUri();
        final HttpRequest httpRequest = HttpRequest.newBuilder()
            .POST(BodyPublishers.ofString(requestBody))
            .uri(requestUri)
            .header("Content-Type", "application/json")
            .build();
        try {
            final HttpResponse<String> figiResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            final JsonNode jsonNodeResponse = objectMapper.readTree(figiResponse.body());
            return StreamSupport.stream(jsonNodeResponse.get(0).spliterator(), false)
                .findAny()
                .map(node -> node.get(0))
                .map(node -> node.get("figi"))
                .map(JsonNode::asText)
                .orElseThrow();
        } catch (Exception e) {
            log.debug("Couldn't retrieve FIGI code from ticker:{}", ticker);
            return null;
        }
    }
}
