package com.intellecteu.onesource.integration.services.client.onesource;

import com.intellecteu.onesource.integration.services.client.onesource.invoker.ApiClient;

import com.intellecteu.onesource.integration.services.client.onesource.dto.LedgerResponseDTO;
import java.time.LocalDateTime;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateStatusDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ReratesDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2024-02-22T19:23:00.293794600Z[Europe/London]")
@Component("com.intellecteu.onesource.integration.services.client.onesource.api.ReratesApi")
public class ReratesApi {
    private ApiClient apiClient;

    public ReratesApi() {
        this(new ApiClient());
    }

    @Autowired
    public ReratesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Read collection of rerates against contract specified by &#x27;contractId&#x27;
     * 
     * <p><b>200</b> - List of rerates
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @return ReratesDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ReratesDTO ledgerContractsContractIdReratesGet(String contractId) throws RestClientException {
        return ledgerContractsContractIdReratesGetWithHttpInfo(contractId).getBody();
    }

    /**
     * Read collection of rerates against contract specified by &#x27;contractId&#x27;
     * 
     * <p><b>200</b> - List of rerates
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;ReratesDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ReratesDTO> ledgerContractsContractIdReratesGetWithHttpInfo(String contractId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReratesGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/rerates").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<ReratesDTO> returnType = new ParameterizedTypeReference<ReratesDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Create a rerate
     * 
     * <p><b>201</b> - The rerate was created
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param body New rerate proposed against contract (required)
     * @param contractId The unique identifier of a contract (required)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void ledgerContractsContractIdReratesPost(RerateProposalDTO body, String contractId) throws RestClientException {
        ResponseEntity<Void> response = ledgerContractsContractIdReratesPostWithHttpInfo(body, contractId);
        if (!HttpStatus.CREATED.equals(response.getStatusCode())) {
            throw new HttpClientErrorException(response.getStatusCode());
        }
    }

    /**
     * Create a rerate
     * 
     * <p><b>201</b> - The rerate was created
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param body New rerate proposed against contract (required)
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> ledgerContractsContractIdReratesPostWithHttpInfo(RerateProposalDTO body, String contractId) throws RestClientException {
        Object postBody = body;
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling ledgerContractsContractIdReratesPost");
        }
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReratesPost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/rerates").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Approve a rerate in \&quot;proposed\&quot; state.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param rerateId The unique identifier of a rerate (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdReratesRerateIdApprovePost(String contractId, String rerateId) throws RestClientException {
        return ledgerContractsContractIdReratesRerateIdApprovePostWithHttpInfo(contractId, rerateId).getBody();
    }

    /**
     * Approve a rerate in \&quot;proposed\&quot; state.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param rerateId The unique identifier of a rerate (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdReratesRerateIdApprovePostWithHttpInfo(String contractId, String rerateId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReratesRerateIdApprovePost");
        }
        // verify the required parameter 'rerateId' is set
        if (rerateId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'rerateId' when calling ledgerContractsContractIdReratesRerateIdApprovePost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("rerateId", rerateId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/rerates/{rerateId}/approve").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<LedgerResponseDTO> returnType = new ParameterizedTypeReference<LedgerResponseDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Cancel a rerate in \&quot;proposed\&quot; state. Original proposer only.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param rerateId The unique identifier of a rerate (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdReratesRerateIdCancelPost(String contractId, String rerateId) throws RestClientException {
        return ledgerContractsContractIdReratesRerateIdCancelPostWithHttpInfo(contractId, rerateId).getBody();
    }

    /**
     * Cancel a rerate in \&quot;proposed\&quot; state. Original proposer only.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param rerateId The unique identifier of a rerate (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdReratesRerateIdCancelPostWithHttpInfo(String contractId, String rerateId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReratesRerateIdCancelPost");
        }
        // verify the required parameter 'rerateId' is set
        if (rerateId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'rerateId' when calling ledgerContractsContractIdReratesRerateIdCancelPost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("rerateId", rerateId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/rerates/{rerateId}/cancel").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<LedgerResponseDTO> returnType = new ParameterizedTypeReference<LedgerResponseDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Cancel a rerate in \&quot;pending\&quot; state. Either party can initiate.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param rerateId The unique identifier of a rerate (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdReratesRerateIdCancelpendingPost(String contractId, String rerateId) throws RestClientException {
        return ledgerContractsContractIdReratesRerateIdCancelpendingPostWithHttpInfo(contractId, rerateId).getBody();
    }

    /**
     * Cancel a rerate in \&quot;pending\&quot; state. Either party can initiate.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param rerateId The unique identifier of a rerate (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdReratesRerateIdCancelpendingPostWithHttpInfo(String contractId, String rerateId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReratesRerateIdCancelpendingPost");
        }
        // verify the required parameter 'rerateId' is set
        if (rerateId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'rerateId' when calling ledgerContractsContractIdReratesRerateIdCancelpendingPost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("rerateId", rerateId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/rerates/{rerateId}/cancelpending").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<LedgerResponseDTO> returnType = new ParameterizedTypeReference<LedgerResponseDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Decline a rerate in \&quot;proposed\&quot; state.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param rerateId The unique identifier of a rerate (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdReratesRerateIdDeclinePost(String contractId, String rerateId) throws RestClientException {
        return ledgerContractsContractIdReratesRerateIdDeclinePostWithHttpInfo(contractId, rerateId).getBody();
    }

    /**
     * Decline a rerate in \&quot;proposed\&quot; state.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param rerateId The unique identifier of a rerate (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdReratesRerateIdDeclinePostWithHttpInfo(String contractId, String rerateId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReratesRerateIdDeclinePost");
        }
        // verify the required parameter 'rerateId' is set
        if (rerateId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'rerateId' when calling ledgerContractsContractIdReratesRerateIdDeclinePost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("rerateId", rerateId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/rerates/{rerateId}/decline").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<LedgerResponseDTO> returnType = new ParameterizedTypeReference<LedgerResponseDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Read a rerate
     * 
     * <p><b>200</b> - The rerate corresponding to the provided \&quot;rerateId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param rerateId The unique identifier of a rerate (required)
     * @return RerateDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public RerateDTO ledgerContractsContractIdReratesRerateIdGet(String contractId, String rerateId) throws RestClientException {
        return ledgerContractsContractIdReratesRerateIdGetWithHttpInfo(contractId, rerateId).getBody();
    }

    /**
     * Read a rerate
     * 
     * <p><b>200</b> - The rerate corresponding to the provided \&quot;rerateId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param rerateId The unique identifier of a rerate (required)
     * @return ResponseEntity&lt;RerateDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<RerateDTO> ledgerContractsContractIdReratesRerateIdGetWithHttpInfo(String contractId, String rerateId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReratesRerateIdGet");
        }
        // verify the required parameter 'rerateId' is set
        if (rerateId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'rerateId' when calling ledgerContractsContractIdReratesRerateIdGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("rerateId", rerateId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/rerates/{rerateId}").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<RerateDTO> returnType = new ParameterizedTypeReference<RerateDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Read collection of rerates
     * 
     * <p><b>200</b> - List of rerates
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @param since Rerates created (since) timestamp UTC (optional)
     * @param before Rerates created (before) timestamp UTC (optional)
     * @param size Number of rerates to be returned. Can be used to facilitate paging (optional)
     * @param rerateStatus Contracts matching status RERATE STATUS (optional)
     * @param figi Rerates with instrument matching FIGI (optional)
     * @param sedol Rerates with instrument matching SEDOL (optional)
     * @param cusip Rerates with instrument matching CUSIP (optional)
     * @param ticker Rerates with instrument matching TICKER (optional)
     * @param isin Rerates with instrument matching ISIN (optional)
     * @param partyId Rerates with a transacting party mathing PARTY ID (optional)
     * @return ReratesDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ReratesDTO ledgerReratesGet(LocalDateTime since, LocalDateTime before, Integer size, RerateStatusDTO rerateStatus, String figi, String sedol, String cusip, String ticker, String isin, String partyId) throws RestClientException {
        return ledgerReratesGetWithHttpInfo(since, before, size, rerateStatus, figi, sedol, cusip, ticker, isin, partyId).getBody();
    }

    /**
     * Read collection of rerates
     * 
     * <p><b>200</b> - List of rerates
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @param since Rerates created (since) timestamp UTC (optional)
     * @param before Rerates created (before) timestamp UTC (optional)
     * @param size Number of rerates to be returned. Can be used to facilitate paging (optional)
     * @param rerateStatus Contracts matching status RERATE STATUS (optional)
     * @param figi Rerates with instrument matching FIGI (optional)
     * @param sedol Rerates with instrument matching SEDOL (optional)
     * @param cusip Rerates with instrument matching CUSIP (optional)
     * @param ticker Rerates with instrument matching TICKER (optional)
     * @param isin Rerates with instrument matching ISIN (optional)
     * @param partyId Rerates with a transacting party mathing PARTY ID (optional)
     * @return ResponseEntity&lt;ReratesDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ReratesDTO> ledgerReratesGetWithHttpInfo(LocalDateTime since, LocalDateTime before, Integer size, RerateStatusDTO rerateStatus, String figi, String sedol, String cusip, String ticker, String isin, String partyId) throws RestClientException {
        Object postBody = null;
        String path = UriComponentsBuilder.fromPath("/ledger/rerates").build().toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "since", since));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "before", before));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "size", size));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "rerateStatus", rerateStatus));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "figi", figi));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sedol", sedol));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "cusip", cusip));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "ticker", ticker));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "isin", isin));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "partyId", partyId));

        final String[] accepts = { 
            "application/json"
         };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {  };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] { "stage_auth" };

        ParameterizedTypeReference<ReratesDTO> returnType = new ParameterizedTypeReference<ReratesDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Read a rerate
     * 
     * <p><b>200</b> - The rerate corresponding to the provided \&quot;rerateId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param rerateId The unique identifier of a rerate (required)
     * @return RerateDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public RerateDTO ledgerReratesRerateIdGet(String rerateId) throws RestClientException {
        return ledgerReratesRerateIdGetWithHttpInfo(rerateId).getBody();
    }

    /**
     * Read a rerate
     * 
     * <p><b>200</b> - The rerate corresponding to the provided \&quot;rerateId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param rerateId The unique identifier of a rerate (required)
     * @return ResponseEntity&lt;RerateDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<RerateDTO> ledgerReratesRerateIdGetWithHttpInfo(String rerateId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'rerateId' is set
        if (rerateId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'rerateId' when calling ledgerReratesRerateIdGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("rerateId", rerateId);
        String path = UriComponentsBuilder.fromPath("/ledger/rerates/{rerateId}").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<RerateDTO> returnType = new ParameterizedTypeReference<RerateDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
}
