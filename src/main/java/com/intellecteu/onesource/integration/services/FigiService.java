package com.intellecteu.onesource.integration.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.exception.FigiRetrievementException;
import com.intellecteu.onesource.integration.model.integrationtoolkit.FigiInfo;
import com.intellecteu.onesource.integration.model.integrationtoolkit.FigiInfoHolder;
import com.intellecteu.onesource.integration.repository.FigiRepository;
import com.intellecteu.onesource.integration.repository.entity.toolkit.FigiEntity;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The purpose of this component is to handle figi retrievement. Not production-ready. Only for the demo as OpenFigi API
 * has limitations. It uses the separate http client that is not used on other business logic to add loose decoupling
 * for the figi retrievement feature. In further development, there is a need to save retrieved figi in the database or
 * cache and execute API calls only on cache miss.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FigiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final FigiRepository figiRepository;


    /**
     * The figi code is a required field to create a loan contract proposal. This method checks figi record in the
     * internal database. IF the figi was not found, send a request to the API. If the figi was retrieved successfully
     * from the API, persist it to the database.
     *
     * @param ticker String
     * @throws FigiRetrievementException if there are multiple or zero figi codes retrieved by the ticker
     */
    @Transactional
    public void findAndSaveFigi(@NotNull String ticker) throws FigiRetrievementException {
        final List<String> figiList = figiRepository.findAllByTicker(ticker);
        log.debug("Figi codes were found in the internal database:{}", figiList.size());
        if (CollectionUtils.isEmpty(figiList)) {
            List<FigiInfo> figiResponseList = requestFigiApi(ticker);
            saveFigi(ticker, figiResponseList);
        }
        if (figiList.size() > 1) {
            throw new FigiRetrievementException("Multiple FIGI codes have been retrieved for the ticker: " + ticker,
                true);
        }
    }

    /**
     * Return figi code by the ticker. Figi code should be managed on the previous steps of the flow. If the figi code
     * was requested from the external resources successfully it must be persisted in the internal database.
     *
     * @param ticker String
     * @return String figi code
     * @throws FigiRetrievementException if no figi code or multiple figi codes retrieved
     */
    public String findFigiByTicker(@NotNull String ticker) throws FigiRetrievementException {
        final List<String> figiList = figiRepository.findAllByTicker(ticker);
        if (CollectionUtils.isEmpty(figiList)) {
            throw new FigiRetrievementException("No FIGI code has been retrieved for the ticker: " + ticker);
        }
        if (figiList.size() > 1) {
            throw new FigiRetrievementException("Multiple FIGI codes have been retrieved for the ticker: " + ticker,
                true);
        }
        return figiList.get(0);
    }

    private String saveFigi(String ticker, List<FigiInfo> figiInfoList) {
        if (CollectionUtils.isEmpty(figiInfoList)) {
            throw new FigiRetrievementException("No FIGI code has been retrieved for the ticker: " + ticker);
        }
        if (figiInfoList.size() > 1) {
            throw new FigiRetrievementException("Multiple FIGI code has been retrieved for the ticker: " + ticker,
                true);
        }
        final String figi = figiInfoList.get(0).getFigi();
        figiRepository.save(new FigiEntity(ticker, figi));
        log.debug("Figi:{} is saved to the database for the ticker:{}", figi, ticker);
        return figi;
    }

    /**
     * Extract figi code from ticker Example ticker to figi code: AAPL -> BBG000B9XRY4
     *
     * @param ticker String ticker
     * @return String figi code or null
     */
    private List<FigiInfo> requestFigiApi(@NonNull String ticker) {
        // todo extract constants to the app configuration file.
        // todo handle OpenFigi API limitations
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
            log.debug("Sending request to the OpenFigi API");
            final HttpResponse<String> figiResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            JsonNode node = objectMapper.readTree(figiResponse.body());
            List<FigiInfoHolder> figiInfoHolder = objectMapper.convertValue(node,
                new TypeReference<List<FigiInfoHolder>>() {
                });
            final List<FigiInfo> figiInfoList = figiInfoHolder.stream().map(FigiInfoHolder::getData)
                .flatMap(List::stream)
                .toList();
            log.debug("Figi codes retrieved from OpenFigi API: {}", figiInfoList.size());
            return figiInfoList;
        } catch (Exception e) {
            log.debug("Couldn't retrieve FIGI code from ticker:{}", ticker);
            return List.of();
        }
    }
}
