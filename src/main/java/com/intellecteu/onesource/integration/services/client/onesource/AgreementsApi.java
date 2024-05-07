package com.intellecteu.onesource.integration.services.client.onesource;

import com.intellecteu.onesource.integration.services.client.onesource.dto.AgreementDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.AgreementsDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.LedgerResponseDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.TradeAgreementDTO;
import com.intellecteu.onesource.integration.services.client.onesource.invoker.ApiClient;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


@Component("com.intellecteu.onesource.integration.services.client.onesource.api.AgreementsApi")
public class AgreementsApi {
    private ApiClient apiClient;

    public AgreementsApi() {
        this(new ApiClient());
    }

    @Autowired
    public AgreementsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Read an agreement
     * 
     * <p><b>200</b> - The trade agreement corresponding to the provided \&quot;agreementId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param agreementId The unique identifier of a trade agreement (required)
     * @return AgreementDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public AgreementDTO ledgerAgreementsAgreementIdGet(String agreementId) throws RestClientException {
        return ledgerAgreementsAgreementIdGetWithHttpInfo(agreementId).getBody();
    }

    /**
     * Read an agreement
     * 
     * <p><b>200</b> - The trade agreement corresponding to the provided \&quot;agreementId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param agreementId The unique identifier of a trade agreement (required)
     * @return ResponseEntity&lt;AgreementDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<AgreementDTO> ledgerAgreementsAgreementIdGetWithHttpInfo(String agreementId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'agreementId' is set
        if (agreementId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'agreementId' when calling ledgerAgreementsAgreementIdGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("agreementId", agreementId);
        String path = UriComponentsBuilder.fromPath("/ledger/agreements/{agreementId}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
         };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {  };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] { "stage_auth" };

        ParameterizedTypeReference<AgreementDTO> returnType = new ParameterizedTypeReference<AgreementDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Read a collection of trade agreements. Defaults to return the last 100 trade agreeements created.
     * 
     * <p><b>200</b> - List of trade agreements
     * <p><b>500</b> - An error occurred
     * @param since Agreements created (since) timestamp UTC (optional)
     * @param before Agreements created (before) timestamp UTC (optional)
     * @param size Number of agreements to be returned. Can be used to facilitate paging (optional)
     * @return AgreementsDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public AgreementsDTO ledgerAgreementsGet(LocalDateTime since, LocalDateTime before, Integer size) throws RestClientException {
        return ledgerAgreementsGetWithHttpInfo(since, before, size).getBody();
    }

    /**
     * Read a collection of trade agreements. Defaults to return the last 100 trade agreeements created.
     * 
     * <p><b>200</b> - List of trade agreements
     * <p><b>500</b> - An error occurred
     * @param since Agreements created (since) timestamp UTC (optional)
     * @param before Agreements created (before) timestamp UTC (optional)
     * @param size Number of agreements to be returned. Can be used to facilitate paging (optional)
     * @return ResponseEntity&lt;AgreementsDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<AgreementsDTO> ledgerAgreementsGetWithHttpInfo(LocalDateTime since, LocalDateTime before, Integer size) throws RestClientException {
        Object postBody = null;
        String path = UriComponentsBuilder.fromPath("/ledger/agreements").build().toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "since", since));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "before", before));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "size", size));

        final String[] accepts = { 
            "application/json"
         };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {  };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] { "stage_auth" };

        ParameterizedTypeReference<AgreementsDTO> returnType = new ParameterizedTypeReference<AgreementsDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Create a trade agreement
     * 
     * <p><b>201</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param body Agreement from a execution venue introduced to the ledger (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerAgreementsPost(TradeAgreementDTO body) throws RestClientException {
        return ledgerAgreementsPostWithHttpInfo(body).getBody();
    }

    /**
     * Create a trade agreement
     * 
     * <p><b>201</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param body Agreement from a execution venue introduced to the ledger (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerAgreementsPostWithHttpInfo(TradeAgreementDTO body) throws RestClientException {
        Object postBody = body;
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling ledgerAgreementsPost");
        }
        String path = UriComponentsBuilder.fromPath("/ledger/agreements").build().toUriString();
        
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

        String[] authNames = new String[] { "stage_auth" };

        ParameterizedTypeReference<LedgerResponseDTO> returnType = new ParameterizedTypeReference<LedgerResponseDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
}
