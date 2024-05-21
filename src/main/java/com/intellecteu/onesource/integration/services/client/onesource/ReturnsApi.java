package com.intellecteu.onesource.integration.services.client.onesource;

import com.intellecteu.onesource.integration.services.client.onesource.dto.LedgerResponseDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ReturnAcknowledgementDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ReturnDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ReturnProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ReturnsDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ReturnsReturnIdBodyDTO;
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


@Component("com.intellecteu.onesource.integration.services.client.onesource.api.ReturnsApi")
public class ReturnsApi {
    private ApiClient apiClient;

    public ReturnsApi() {
        this(new ApiClient());
    }

    @Autowired
    public ReturnsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Read collection of returns against contract specified by &#x27;contractId&#x27;
     * 
     * <p><b>200</b> - List of returns
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @return ReturnsDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ReturnsDTO ledgerContractsContractIdReturnsGet(String contractId) throws RestClientException {
        return ledgerContractsContractIdReturnsGetWithHttpInfo(contractId).getBody();
    }

    /**
     * Read collection of returns against contract specified by &#x27;contractId&#x27;
     * 
     * <p><b>200</b> - List of returns
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;ReturnsDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ReturnsDTO> ledgerContractsContractIdReturnsGetWithHttpInfo(String contractId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReturnsGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/returns").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<ReturnsDTO> returnType = new ParameterizedTypeReference<ReturnsDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Create a return
     * 
     * <p><b>201</b> - The return was created
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param body New return proposed against contract (required)
     * @param contractId The unique identifier of a contract (required)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void ledgerContractsContractIdReturnsPost(ReturnProposalDTO body, String contractId) throws RestClientException {
        ledgerContractsContractIdReturnsPostWithHttpInfo(body, contractId);
    }

    /**
     * Create a return
     * 
     * <p><b>201</b> - The return was created
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param body New return proposed against contract (required)
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> ledgerContractsContractIdReturnsPostWithHttpInfo(ReturnProposalDTO body, String contractId) throws RestClientException {
        Object postBody = body;
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling ledgerContractsContractIdReturnsPost");
        }
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReturnsPost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/returns").buildAndExpand(uriVariables).toUriString();
        
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
     * Acknowledge a pending return. No change to status.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param body Acknowledge return with positive/negative intent (required)
     * @param contractId The unique identifier of a contract (required)
     * @param returnId The unique identifier of a return (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdReturnsReturnIdAcknowledgePost(ReturnAcknowledgementDTO body, String contractId, String returnId) throws RestClientException {
        return ledgerContractsContractIdReturnsReturnIdAcknowledgePostWithHttpInfo(body, contractId, returnId).getBody();
    }

    /**
     * Acknowledge a pending return. No change to status.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param body Acknowledge return with positive/negative intent (required)
     * @param contractId The unique identifier of a contract (required)
     * @param returnId The unique identifier of a return (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdReturnsReturnIdAcknowledgePostWithHttpInfo(ReturnAcknowledgementDTO body, String contractId, String returnId) throws RestClientException {
        Object postBody = body;
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling ledgerContractsContractIdReturnsReturnIdAcknowledgePost");
        }
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReturnsReturnIdAcknowledgePost");
        }
        // verify the required parameter 'returnId' is set
        if (returnId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'returnId' when calling ledgerContractsContractIdReturnsReturnIdAcknowledgePost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("returnId", returnId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/returns/{returnId}/acknowledge").buildAndExpand(uriVariables).toUriString();
        
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
    /**
     * Cancel a return in \&quot;proposed\&quot; or \&quot;pending\&quot; state. Original proposer only.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param returnId The unique identifier of a return (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdReturnsReturnIdCancelPost(String contractId, String returnId) throws RestClientException {
        return ledgerContractsContractIdReturnsReturnIdCancelPostWithHttpInfo(contractId, returnId).getBody();
    }

    /**
     * Cancel a return in \&quot;proposed\&quot; or \&quot;pending\&quot; state. Original proposer only.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param returnId The unique identifier of a return (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdReturnsReturnIdCancelPostWithHttpInfo(String contractId, String returnId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReturnsReturnIdCancelPost");
        }
        // verify the required parameter 'returnId' is set
        if (returnId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'returnId' when calling ledgerContractsContractIdReturnsReturnIdCancelPost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("returnId", returnId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/returns/{returnId}/cancel").buildAndExpand(uriVariables).toUriString();
        
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
     * Read a return
     * 
     * <p><b>200</b> - The return corresponding to the provided \&quot;returnId\&quot;
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param returnId The unique identifier of a return (required)
     * @return ReturnDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ReturnDTO ledgerContractsContractIdReturnsReturnIdGet(String contractId, String returnId) throws RestClientException {
        return ledgerContractsContractIdReturnsReturnIdGetWithHttpInfo(contractId, returnId).getBody();
    }

    /**
     * Read a return
     * 
     * <p><b>200</b> - The return corresponding to the provided \&quot;returnId\&quot;
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param returnId The unique identifier of a return (required)
     * @return ResponseEntity&lt;ReturnDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ReturnDTO> ledgerContractsContractIdReturnsReturnIdGetWithHttpInfo(String contractId, String returnId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReturnsReturnIdGet");
        }
        // verify the required parameter 'returnId' is set
        if (returnId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'returnId' when calling ledgerContractsContractIdReturnsReturnIdGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("returnId", returnId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/returns/{returnId}").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<ReturnDTO> returnType = new ParameterizedTypeReference<ReturnDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Update unilateral fields on a return
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param returnId The unique identifier of a return (required)
     * @param body  (optional)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdReturnsReturnIdPatch(String contractId, String returnId, ReturnsReturnIdBodyDTO body) throws RestClientException {
        return ledgerContractsContractIdReturnsReturnIdPatchWithHttpInfo(contractId, returnId, body).getBody();
    }

    /**
     * Update unilateral fields on a return
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param returnId The unique identifier of a return (required)
     * @param body  (optional)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdReturnsReturnIdPatchWithHttpInfo(String contractId, String returnId, ReturnsReturnIdBodyDTO body) throws RestClientException {
        Object postBody = body;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdReturnsReturnIdPatch");
        }
        // verify the required parameter 'returnId' is set
        if (returnId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'returnId' when calling ledgerContractsContractIdReturnsReturnIdPatch");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("returnId", returnId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/returns/{returnId}").buildAndExpand(uriVariables).toUriString();
        
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
        return apiClient.invokeAPI(path, HttpMethod.PATCH, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Read collection of returns
     * 
     * <p><b>200</b> - List of returns
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @return ReturnsDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ReturnsDTO ledgerReturnsGet() throws RestClientException {
        return ledgerReturnsGetWithHttpInfo().getBody();
    }

    /**
     * Read collection of returns
     * 
     * <p><b>200</b> - List of returns
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @return ResponseEntity&lt;ReturnsDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ReturnsDTO> ledgerReturnsGetWithHttpInfo() throws RestClientException {
        Object postBody = null;
        String path = UriComponentsBuilder.fromPath("/ledger/returns").build().toUriString();
        
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

        ParameterizedTypeReference<ReturnsDTO> returnType = new ParameterizedTypeReference<ReturnsDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Read a return
     * 
     * <p><b>200</b> - The return corresponding to the provided \&quot;returnId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param returnId The unique identifier of a return (required)
     * @return ReturnDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ReturnDTO ledgerReturnsReturnIdGet(String returnId) throws RestClientException {
        return ledgerReturnsReturnIdGetWithHttpInfo(returnId).getBody();
    }

    /**
     * Read a return
     * 
     * <p><b>200</b> - The return corresponding to the provided \&quot;returnId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param returnId The unique identifier of a return (required)
     * @return ResponseEntity&lt;ReturnDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ReturnDTO> ledgerReturnsReturnIdGetWithHttpInfo(String returnId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'returnId' is set
        if (returnId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'returnId' when calling ledgerReturnsReturnIdGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("returnId", returnId);
        String path = UriComponentsBuilder.fromPath("/ledger/returns/{returnId}").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<ReturnDTO> returnType = new ParameterizedTypeReference<ReturnDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
}
