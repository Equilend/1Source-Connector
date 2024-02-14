package com.intellecteu.onesource.integration.services.client.spire;

import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryRequest;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponseInstructionDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.instruction.InstructionDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.instruction.SResponseInstructionDTO;
import com.intellecteu.onesource.integration.services.client.spire.invoker.ApiClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class InstructionSpireApiClient {

    private final ApiClient apiClient;
    private final String clientId;

    public InstructionSpireApiClient(ApiClient spireApiClient,
        @Value("${spire.username}") String clientId) {
        this.apiClient = spireApiClient;
        this.clientId = clientId;
    }

    public ResponseEntity<SResponseNQueryResponseInstructionDTO> getSettlementDetails(NQueryRequest nQueryRequest) {
        final ResponseEntity<SResponseNQueryResponseInstructionDTO> response = getInstructionUsingPOSTWithHttpInfo(
            null, clientId, nQueryRequest);
        if (HttpStatus.CREATED == response.getStatusCode()) {
            // temporal throw an exception to record until requirements will be retrieved how to handle 201 status
            throw new HttpClientErrorException(HttpStatus.CREATED);
        }
        return response;
    }

    public void updateInstruction(InstructionDTO instruction) {
        updateInstructionUsingPUTWithHttpInfo(null, instruction, instruction.getInstructionId());
    }

    /**
     * getInstruction
     *
     * <p><b>200</b> - OK
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     *
     * @param accessToken Access token generated during user authentication (required)
     * @param query query (required)
     * @return ResponseEntity&lt;SResponseNQueryResponseInstructionDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    private ResponseEntity<SResponseNQueryResponseInstructionDTO> getInstructionUsingPOSTWithHttpInfo(
        String accessToken,
        String clientId, NQueryRequest query) throws RestClientException {
        Object postBody = query;

        String path = UriComponentsBuilder.fromPath("/rds/static/instruction").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (accessToken != null) {
            headerParams.add("access_token", apiClient.parameterToString(accessToken));
        }

        if (clientId != null) {
            headerParams.add("client_id", apiClient.parameterToString(clientId));
        }

        final String[] accepts = {
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {
            "application/json"
        };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<SResponseNQueryResponseInstructionDTO> returnType =
            new ParameterizedTypeReference<>() {
            };
        log.debug("Sending {} request to: {}", HttpMethod.POST, apiClient.getBasePath() + path);
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams,
            formParams, accept, contentType, authNames, returnType);
    }

    /**
     * updateInstruction
     *
     * <p><b>200</b> - OK
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     *
     * @param accessToken Access token generated during user authentication (required)
     * @param instructionDTO instructionDTO (required)
     * @param instructionId instructionId (required)
     * @return ResponseEntity&lt;SResponseInstructionDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    private ResponseEntity<SResponseInstructionDTO> updateInstructionUsingPUTWithHttpInfo(String accessToken,
        InstructionDTO instructionDTO, Long instructionId) throws RestClientException {
        Object postBody = instructionDTO;

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("instructionId", instructionId);
        String path = UriComponentsBuilder.fromPath("/rds/static/instruction/{instructionId}")
            .buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (accessToken != null) {
            headerParams.add("access_token", apiClient.parameterToString(accessToken));
        }

        final String[] accepts = {
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {
            "application/json"
        };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<SResponseInstructionDTO> returnType = new ParameterizedTypeReference<>() {
        };
        log.debug("Sending {} request to update settlement instruction: {} to: {}", HttpMethod.PUT,
            instructionId, apiClient.getBasePath() + path);
        return apiClient.invokeAPI(path, HttpMethod.PUT, queryParams, postBody, headerParams,
            formParams, accept, contentType, authNames, returnType);
    }

}
