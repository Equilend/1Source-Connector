package com.intellecteu.onesource.integration.services.client.onesource;

import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalApprovalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.LedgerResponseDTO;
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
     * Read a specific contract the user is authorized to access
     *
     * <p><b>200</b> - The contract corresponding to the provided \&quot;contractId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     *
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
     *
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;ContractDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<ContractDTO> ledgerContractsContractIdGetWithHttpInfo(String contractId)
        throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'contractId' when calling ledgerContractsContractIdGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}").buildAndExpand(uriVariables)
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

        ParameterizedTypeReference<ContractDTO> returnType = new ParameterizedTypeReference<ContractDTO>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
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

    /**
     * Approve a contract in \&quot;proposed\&quot; state
     *
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>409</b> - Conflict with the state of the resource
     * <p><b>500</b> - An error occurred
     *
     * @param body Update settlement instructions on an existing contract (required)
     * @param contractId The unique identifier of a contract (required)
     * @return LedgerResponseDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public LedgerResponseDTO ledgerContractsContractIdApprovePost(ContractProposalApprovalDTO body, String contractId)
        throws RestClientException {
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
     *
     * @param body Update settlement instructions on an existing contract (required)
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdApprovePostWithHttpInfo(
        ContractProposalApprovalDTO body, String contractId) throws RestClientException {
        Object postBody = body;
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'body' when calling ledgerContractsContractIdApprovePost");
        }
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'contractId' when calling ledgerContractsContractIdApprovePost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/approve")
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

        ParameterizedTypeReference<LedgerResponseDTO> returnType = new ParameterizedTypeReference<LedgerResponseDTO>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    /**
     * Decline a contract in \&quot;proposed\&quot; state
     *
     * <p><b>200</b> - Operation was successful
     * <p><b>400</b> - Bad request or more information needed
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     *
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
     *
     * @param contractId The unique identifier of a contract (required)
     * @return ResponseEntity&lt;LedgerResponseDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<LedgerResponseDTO> ledgerContractsContractIdDeclinePostWithHttpInfo(String contractId)
        throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'contractId' when calling ledgerContractsContractIdDeclinePost");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/decline")
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
}
