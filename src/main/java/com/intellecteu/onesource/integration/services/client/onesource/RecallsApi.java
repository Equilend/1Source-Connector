package com.intellecteu.onesource.integration.services.client.onesource;

import com.intellecteu.onesource.integration.services.client.onesource.dto.LedgerResponseDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RecallDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RecallProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RecallsDTO;
import com.intellecteu.onesource.integration.services.client.onesource.invoker.ApiClient;
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


@Component("com.intellecteu.onesource.integration.services.client.onesource.api.RecallsApi")
public class RecallsApi {

    private ApiClient apiClient;

    public RecallsApi() {
        this(new ApiClient());
    }

    @Autowired
    public RecallsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Read collection of recalls against contract specified by &#x27;contractId&#x27;
     *
     * <p><b>200</b> - List of recalls
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     *
     * @param contractId The unique identifier of a contract (required)
     * @return RecallsDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public RecallsDTO ledgerContractsContractIdRecallsGet(String contractId) throws RestClientException {
        return ledgerContractsContractIdRecallsGetWithHttpInfo(contractId).getBody();
    }

    /**
     * Read collection of recalls against contract specified by &#x27;contractId&#x27;
     *
     * <p><b>200</b> - List of recalls
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     *
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;RecallsDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<RecallsDTO> ledgerContractsContractIdRecallsGetWithHttpInfo(String contractId)
        throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'contractId' when calling ledgerContractsContractIdRecallsGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/recalls")
            .buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{"stage_auth"};

        ParameterizedTypeReference<RecallsDTO> returnType = new ParameterizedTypeReference<RecallsDTO>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    /**
     * Create a recall
     *
     * <p><b>201</b> - The recall was created
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     *
     * @param body New recall proposed against contract (required)
     * @param contractId The unique identifier of a contract (required)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void ledgerContractsContractIdRecallsPost(RecallProposalDTO body, String contractId)
        throws RestClientException {
        ledgerContractsContractIdRecallsPostWithHttpInfo(body, contractId);
    }

    /**
     * Create a recall
     *
     * <p><b>201</b> - The recall was created
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     *
     * @param body New recall proposed against contract (required)
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> ledgerContractsContractIdRecallsPostWithHttpInfo(RecallProposalDTO body,
        String contractId) throws RestClientException {
        Object postBody = body;
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'body' when calling ledgerContractsContractIdRecallsPost");
        }
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'contractId' when calling ledgerContractsContractIdRecallsPost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/recalls")
            .buildAndExpand(uriVariables).toUriString();

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

        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    /**
     * Cancel a recall in \&quot;proposed\&quot; state. Original proposer only.
     *
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     *
     * @param contractId The unique identifier of a contract (required)
     * @param recallId The unique identifier of a recall (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdRecallsRecallIdCancelPost(String contractId, String recallId)
        throws RestClientException {
        return ledgerContractsContractIdRecallsRecallIdCancelPostWithHttpInfo(contractId, recallId).getBody();
    }

    /**
     * Cancel a recall in \&quot;proposed\&quot; state. Original proposer only.
     *
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     *
     * @param contractId The unique identifier of a contract (required)
     * @param recallId The unique identifier of a recall (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdRecallsRecallIdCancelPostWithHttpInfo(
        String contractId, String recallId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'contractId' when calling ledgerContractsContractIdRecallsRecallIdCancelPost");
        }
        // verify the required parameter 'recallId' is set
        if (recallId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'recallId' when calling ledgerContractsContractIdRecallsRecallIdCancelPost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("recallId", recallId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/recalls/{recallId}/cancel")
            .buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{"stage_auth"};

        ParameterizedTypeReference<LedgerResponseDTO> returnType = new ParameterizedTypeReference<LedgerResponseDTO>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    /**
     * Read a recall
     *
     * <p><b>200</b> - The recall corresponding to the provided \&quot;recallId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     *
     * @param contractId The unique identifier of a contract (required)
     * @param recallId The unique identifier of a recall (required)
     * @return RecallDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public RecallDTO ledgerContractsContractIdRecallsRecallIdGet(String contractId, String recallId)
        throws RestClientException {
        return ledgerContractsContractIdRecallsRecallIdGetWithHttpInfo(contractId, recallId).getBody();
    }

    /**
     * Read a recall
     *
     * <p><b>200</b> - The recall corresponding to the provided \&quot;recallId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     *
     * @param contractId The unique identifier of a contract (required)
     * @param recallId The unique identifier of a recall (required)
     * @return ResponseEntity&lt;RecallDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<RecallDTO> ledgerContractsContractIdRecallsRecallIdGetWithHttpInfo(String contractId,
        String recallId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'contractId' when calling ledgerContractsContractIdRecallsRecallIdGet");
        }
        // verify the required parameter 'recallId' is set
        if (recallId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'recallId' when calling ledgerContractsContractIdRecallsRecallIdGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("recallId", recallId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/recalls/{recallId}")
            .buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{"stage_auth"};

        ParameterizedTypeReference<RecallDTO> returnType = new ParameterizedTypeReference<RecallDTO>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    /**
     * Read collection of recalls
     *
     * <p><b>200</b> - List of recalls
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     *
     * @return RecallsDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public RecallsDTO ledgerRecallsGet() throws RestClientException {
        return ledgerRecallsGetWithHttpInfo().getBody();
    }

    /**
     * Read collection of recalls
     *
     * <p><b>200</b> - List of recalls
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     *
     * @return ResponseEntity&lt;RecallsDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<RecallsDTO> ledgerRecallsGetWithHttpInfo() throws RestClientException {
        Object postBody = null;
        String path = UriComponentsBuilder.fromPath("/ledger/recalls").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{"stage_auth"};

        ParameterizedTypeReference<RecallsDTO> returnType = new ParameterizedTypeReference<RecallsDTO>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    /**
     * Read a recall
     *
     * <p><b>200</b> - The recall corresponding to the provided \&quot;recallId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     *
     * @param recallId The unique identifier of a recall (required)
     * @return RecallDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public RecallDTO ledgerRecallsRecallIdGet(String recallId) throws RestClientException {
        return ledgerRecallsRecallIdGetWithHttpInfo(recallId).getBody();
    }

    /**
     * Read a recall
     *
     * <p><b>200</b> - The recall corresponding to the provided \&quot;recallId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     *
     * @param recallId The unique identifier of a recall (required)
     * @return ResponseEntity&lt;RecallDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<RecallDTO> ledgerRecallsRecallIdGetWithHttpInfo(String recallId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'recallId' is set
        if (recallId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'recallId' when calling ledgerRecallsRecallIdGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("recallId", recallId);
        String path = UriComponentsBuilder.fromPath("/ledger/recalls/{recallId}").buildAndExpand(uriVariables)
            .toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{"stage_auth"};

        ParameterizedTypeReference<RecallDTO> returnType = new ParameterizedTypeReference<RecallDTO>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }
}
