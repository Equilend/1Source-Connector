package com.intellecteu.onesource.integration.services.client.onesource;

import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.LedgerResponseDTO;
import com.intellecteu.onesource.integration.services.client.onesource.invoker.ApiClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2024-03-18T20:46:19.463975300+02:00[Europe/Kiev]")
@Component("com.intellecteu.onesource.integration.services.client.onesource.api.ContractsApi")
public class ContractsApi {

    private ApiClient apiClient;

    public ContractsApi() {
        this(new ApiClient());
    }

    @Autowired
    public ContractsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }


    /**
     * Create a contract in \&quot;proposal\&quot; state. Normally done by the Lend side
     *
     * <p><b>201</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     *
     * @param body New contract proposed for inclusion in the ledger (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsPost(ContractProposalDTO body) throws RestClientException {
        return ledgerContractsPostWithHttpInfo(body);
    }

    /**
     * Create a contract in \&quot;proposal\&quot; state. Normally done by the Lend side
     *
     * <p><b>201</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     *
     * @param body New contract proposed for inclusion in the ledger (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsPostWithHttpInfo(ContractProposalDTO body)
        throws RestClientException {
        Object postBody = body;
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'body' when calling ledgerContractsPost");
        }
        String path = UriComponentsBuilder.fromPath("/ledger/contracts").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {
            "application/json"
        };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{"stage_auth"};

        ParameterizedTypeReference<LedgerResponseDTO> returnType = new ParameterizedTypeReference<LedgerResponseDTO>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }
}
