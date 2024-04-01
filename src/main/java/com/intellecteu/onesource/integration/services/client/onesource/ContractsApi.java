package com.intellecteu.onesource.integration.services.client.onesource;

import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalApprovalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractSplitDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractSplitLotDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractStatusDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractsContractIdBodyDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractsDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.LedgerResponseDTO;
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
     * Approve a contract in \&quot;proposed\&quot; state
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>409</b> - Conflict with the state of the resource
     * <p><b>500</b> - An error occurred
     * @param body Update settlement instructions on an existing contract (required)
     * @param contractId The unique identifier of a contract (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdApprovePost(ContractProposalApprovalDTO body, String contractId) throws RestClientException {
        return ledgerContractsContractIdApprovePostWithHttpInfo(body, contractId).getBody();
    }

    /**
     * Approve a contract in \&quot;proposed\&quot; state
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>409</b> - Conflict with the state of the resource
     * <p><b>500</b> - An error occurred
     * @param body Update settlement instructions on an existing contract (required)
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdApprovePostWithHttpInfo(ContractProposalApprovalDTO body, String contractId) throws RestClientException {
        Object postBody = body;
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling ledgerContractsContractIdApprovePost");
        }
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdApprovePost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/approve").buildAndExpand(uriVariables).toUriString();
        
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
     * Cancel a contract in \&quot;proposed\&quot; state. Original proposer only.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdCancelPost(String contractId) throws RestClientException {
        return ledgerContractsContractIdCancelPostWithHttpInfo(contractId).getBody();
    }

    /**
     * Cancel a contract in \&quot;proposed\&quot; state. Original proposer only.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdCancelPostWithHttpInfo(String contractId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdCancelPost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/cancel").buildAndExpand(uriVariables).toUriString();
        
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
     * Cancel a contract in \&quot;pending\&quot; state. Either party can initiate.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdCancelpendingPost(String contractId) throws RestClientException {
        return ledgerContractsContractIdCancelpendingPostWithHttpInfo(contractId).getBody();
    }

    /**
     * Cancel a contract in \&quot;pending\&quot; state. Either party can initiate.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdCancelpendingPostWithHttpInfo(String contractId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdCancelpendingPost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/cancelpending").buildAndExpand(uriVariables).toUriString();
        
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
     * Decline a contract in \&quot;proposed\&quot; state
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdDeclinePost(String contractId) throws RestClientException {
        return ledgerContractsContractIdDeclinePostWithHttpInfo(contractId).getBody();
    }

    /**
     * Decline a contract in \&quot;proposed\&quot; state
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdDeclinePostWithHttpInfo(String contractId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdDeclinePost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/decline").buildAndExpand(uriVariables).toUriString();
        
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
     * Read a specific contract the user is authorized to access
     * 
     * <p><b>200</b> - The contract corresponding to the provided \&quot;contractId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @return ContractDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ContractDTO ledgerContractsContractIdGet(String contractId) throws RestClientException {
        return ledgerContractsContractIdGetWithHttpInfo(contractId).getBody();
    }

    /**
     * Read a specific contract the user is authorized to access
     * 
     * <p><b>200</b> - The contract corresponding to the provided \&quot;contractId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;ContractDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ContractDTO> ledgerContractsContractIdGetWithHttpInfo(String contractId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<ContractDTO> returnType = new ParameterizedTypeReference<ContractDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Return an ordered history of this contract. Each contract has a reference event that triggered a new version.
     * 
     * <p><b>200</b> - List of contracts
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param since Events (since) timestamp UTC (optional)
     * @param before Events (before) timestamp UTC (optional)
     * @param size Number of events to be returned. Can be used to facilitate paging (optional)
     * @return ContractsDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ContractsDTO ledgerContractsContractIdHistoryGet(String contractId, LocalDateTime since, LocalDateTime before, Integer size) throws RestClientException {
        return ledgerContractsContractIdHistoryGetWithHttpInfo(contractId, since, before, size).getBody();
    }

    /**
     * Return an ordered history of this contract. Each contract has a reference event that triggered a new version.
     * 
     * <p><b>200</b> - List of contracts
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param since Events (since) timestamp UTC (optional)
     * @param before Events (before) timestamp UTC (optional)
     * @param size Number of events to be returned. Can be used to facilitate paging (optional)
     * @return ResponseEntity&lt;ContractsDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ContractsDTO> ledgerContractsContractIdHistoryGetWithHttpInfo(String contractId, LocalDateTime since, LocalDateTime before, Integer size) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdHistoryGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/history").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<ContractsDTO> returnType = new ParameterizedTypeReference<ContractsDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Update unilateral fields in a contract
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param body  (optional)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdPatch(String contractId, ContractsContractIdBodyDTO body) throws RestClientException {
        return ledgerContractsContractIdPatchWithHttpInfo(contractId, body).getBody();
    }

    /**
     * Update unilateral fields in a contract
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param body  (optional)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdPatchWithHttpInfo(String contractId, ContractsContractIdBodyDTO body) throws RestClientException {
        Object postBody = body;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdPatch");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}").buildAndExpand(uriVariables).toUriString();
        
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
     * Approve a contract split in \&quot;proposed\&quot; state.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param contractSplitId The unique identifier of a proposed contract split (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdSplitContractSplitIdApprovePost(String contractId, String contractSplitId) throws RestClientException {
        return ledgerContractsContractIdSplitContractSplitIdApprovePostWithHttpInfo(contractId, contractSplitId).getBody();
    }

    /**
     * Approve a contract split in \&quot;proposed\&quot; state.
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param contractSplitId The unique identifier of a proposed contract split (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdSplitContractSplitIdApprovePostWithHttpInfo(String contractId, String contractSplitId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdSplitContractSplitIdApprovePost");
        }
        // verify the required parameter 'contractSplitId' is set
        if (contractSplitId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractSplitId' when calling ledgerContractsContractIdSplitContractSplitIdApprovePost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("contractSplitId", contractSplitId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/split/{contractSplitId}/approve").buildAndExpand(uriVariables).toUriString();
        
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
     * Retrieve a contract split.
     * 
     * <p><b>200</b> - The contract split corresponding to the provided \&quot;contractSplitId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param contractSplitId The unique identifier of a contract split (required)
     * @return ContractSplitDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ContractSplitDTO ledgerContractsContractIdSplitContractSplitIdGet(String contractId, String contractSplitId) throws RestClientException {
        return ledgerContractsContractIdSplitContractSplitIdGetWithHttpInfo(contractId, contractSplitId).getBody();
    }

    /**
     * Retrieve a contract split.
     * 
     * <p><b>200</b> - The contract split corresponding to the provided \&quot;contractSplitId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param contractSplitId The unique identifier of a contract split (required)
     * @return ResponseEntity&lt;ContractSplitDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ContractSplitDTO> ledgerContractsContractIdSplitContractSplitIdGetWithHttpInfo(String contractId, String contractSplitId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdSplitContractSplitIdGet");
        }
        // verify the required parameter 'contractSplitId' is set
        if (contractSplitId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractSplitId' when calling ledgerContractsContractIdSplitContractSplitIdGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("contractSplitId", contractSplitId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/split/{contractSplitId}").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<ContractSplitDTO> returnType = new ParameterizedTypeReference<ContractSplitDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Split an open contract into multiple lots
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>409</b> - Conflict with the state of the resource
     * <p><b>500</b> - An error occurred
     * @param body Split proposed against contract (required)
     * @param contractId The unique identifier of a contract (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdSplitPost(List<ContractSplitLotDTO> body, String contractId) throws RestClientException {
        return ledgerContractsContractIdSplitPostWithHttpInfo(body, contractId).getBody();
    }

    /**
     * Split an open contract into multiple lots
     * 
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>409</b> - Conflict with the state of the resource
     * <p><b>500</b> - An error occurred
     * @param body Split proposed against contract (required)
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdSplitPostWithHttpInfo(List<ContractSplitLotDTO> body, String contractId) throws RestClientException {
        Object postBody = body;
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling ledgerContractsContractIdSplitPost");
        }
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdSplitPost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/split").buildAndExpand(uriVariables).toUriString();
        
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
     * Read a collection of contracts. Defaults to return the last 100 contracts created.
     * 
     * <p><b>200</b> - List of contracts
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @param since Contracts created (since) timestamp UTC (optional)
     * @param before Contracts created (before) timestamp UTC (optional)
     * @param size Number of contracts to be returned. Can be used to facilitate paging (optional)
     * @param contractStatus Contracts with status matching CONTRACT STATUS (optional)
     * @param figi Contracts with instrument matching FIGI (optional)
     * @param sedol Contracts with instrument matching SEDOL (optional)
     * @param cusip Contracts with instrument matching CUSIP (optional)
     * @param ticker Contracts with instrument matching TICKER (optional)
     * @param isin Contracts with instrument matching ISIN (optional)
     * @param internalRefId Contracts with internalRef:internalRefId matching INTERNAL REF ID (optional)
     * @param partyId Contracts with a transacting party mathing PARTY ID (optional)
     * @return ContractsDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ContractsDTO ledgerContractsGet(LocalDateTime since, LocalDateTime before, Integer size, ContractStatusDTO contractStatus, String figi, String sedol, String cusip, String ticker, String isin, String internalRefId, String partyId) throws RestClientException {
        return ledgerContractsGetWithHttpInfo(since, before, size, contractStatus, figi, sedol, cusip, ticker, isin, internalRefId, partyId).getBody();
    }

    /**
     * Read a collection of contracts. Defaults to return the last 100 contracts created.
     * 
     * <p><b>200</b> - List of contracts
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @param since Contracts created (since) timestamp UTC (optional)
     * @param before Contracts created (before) timestamp UTC (optional)
     * @param size Number of contracts to be returned. Can be used to facilitate paging (optional)
     * @param contractStatus Contracts with status matching CONTRACT STATUS (optional)
     * @param figi Contracts with instrument matching FIGI (optional)
     * @param sedol Contracts with instrument matching SEDOL (optional)
     * @param cusip Contracts with instrument matching CUSIP (optional)
     * @param ticker Contracts with instrument matching TICKER (optional)
     * @param isin Contracts with instrument matching ISIN (optional)
     * @param internalRefId Contracts with internalRef:internalRefId matching INTERNAL REF ID (optional)
     * @param partyId Contracts with a transacting party mathing PARTY ID (optional)
     * @return ResponseEntity&lt;ContractsDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ContractsDTO> ledgerContractsGetWithHttpInfo(LocalDateTime since, LocalDateTime before, Integer size, ContractStatusDTO contractStatus, String figi, String sedol, String cusip, String ticker, String isin, String internalRefId, String partyId) throws RestClientException {
        Object postBody = null;
        String path = UriComponentsBuilder.fromPath("/ledger/contracts").build().toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "since", since));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "before", before));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "size", size));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "contractStatus", contractStatus));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "figi", figi));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sedol", sedol));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "cusip", cusip));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "ticker", ticker));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "isin", isin));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "internalRefId", internalRefId));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "partyId", partyId));

        final String[] accepts = { 
            "application/json"
         };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {  };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] { "stage_auth" };

        ParameterizedTypeReference<ContractsDTO> returnType = new ParameterizedTypeReference<ContractsDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Create a contract in \&quot;proposal\&quot; state. Normally done by the Lend side
     * 
     * <p><b>201</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @param body New contract proposed for inclusion in the ledger (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsPost(ContractProposalDTO body) throws RestClientException {
        return ledgerContractsPostWithHttpInfo(body).getBody();
    }

    /**
     * Create a contract in \&quot;proposal\&quot; state. Normally done by the Lend side
     * 
     * <p><b>201</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @param body New contract proposed for inclusion in the ledger (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsPostWithHttpInfo(ContractProposalDTO body) throws RestClientException {
        Object postBody = body;
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling ledgerContractsPost");
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

        String[] authNames = new String[] { "stage_auth" };

        ParameterizedTypeReference<LedgerResponseDTO> returnType = new ParameterizedTypeReference<LedgerResponseDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
}
