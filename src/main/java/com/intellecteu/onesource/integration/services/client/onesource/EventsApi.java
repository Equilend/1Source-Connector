package com.intellecteu.onesource.integration.services.client.onesource;

import com.intellecteu.onesource.integration.services.client.onesource.invoker.ApiClient;

import com.intellecteu.onesource.integration.services.client.onesource.dto.EventDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.EventTypeDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.EventsDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.LedgerResponseDTO;
import java.time.LocalDateTime;

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


@Component("com.intellecteu.onesource.integration.services.client.onesource.api.EventsApi")
public class EventsApi {
    private ApiClient apiClient;

    public EventsApi() {
        this(new ApiClient());
    }

    @Autowired
    public EventsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Read an event
     * 
     * <p><b>200</b> - The event corresponding to the provided \&quot;eventId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param eventId The unique identifier of an Event (required)
     * @return EventDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public EventDTO ledgerContractsContractIdEventsEventIdGet(String contractId, Integer eventId) throws RestClientException {
        return ledgerContractsContractIdEventsEventIdGetWithHttpInfo(contractId, eventId).getBody();
    }

    /**
     * Read an event
     * 
     * <p><b>200</b> - The event corresponding to the provided \&quot;eventId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param eventId The unique identifier of an Event (required)
     * @return ResponseEntity&lt;EventDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<EventDTO> ledgerContractsContractIdEventsEventIdGetWithHttpInfo(String contractId, Integer eventId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdEventsEventIdGet");
        }
        // verify the required parameter 'eventId' is set
        if (eventId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'eventId' when calling ledgerContractsContractIdEventsEventIdGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        uriVariables.put("eventId", eventId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/events/{eventId}").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<EventDTO> returnType = new ParameterizedTypeReference<EventDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Read collection of events against a specific contract. With no parameters returns events since start of current day.
     * 
     * <p><b>200</b> - List of events
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param eventType Filter by event type (optional)
     * @param fromEventId  (optional)
     * @param since Events (since) timestamp UTC (optional)
     * @param before Events (before) timestamp UTC (optional)
     * @param size Number of events to be returned. Can be used to facilitate paging (optional)
     * @return EventsDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public EventsDTO ledgerContractsContractIdEventsGet(String contractId, List<EventTypeDTO> eventType, Integer fromEventId, LocalDateTime since, LocalDateTime before, Integer size) throws RestClientException {
        return ledgerContractsContractIdEventsGetWithHttpInfo(contractId, eventType, fromEventId, since, before, size).getBody();
    }

    /**
     * Read collection of events against a specific contract. With no parameters returns events since start of current day.
     * 
     * <p><b>200</b> - List of events
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @param contractId The unique identifier of a contract (required)
     * @param eventType Filter by event type (optional)
     * @param fromEventId  (optional)
     * @param since Events (since) timestamp UTC (optional)
     * @param before Events (before) timestamp UTC (optional)
     * @param size Number of events to be returned. Can be used to facilitate paging (optional)
     * @return ResponseEntity&lt;EventsDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<EventsDTO> ledgerContractsContractIdEventsGetWithHttpInfo(String contractId, List<EventTypeDTO> eventType, Integer fromEventId, LocalDateTime since, LocalDateTime before, Integer size) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'contractId' is set
        if (contractId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'contractId' when calling ledgerContractsContractIdEventsGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("contractId", contractId);
        String path = UriComponentsBuilder.fromPath("/ledger/contracts/{contractId}/events").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
        queryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("multi".toUpperCase()), "eventType", eventType));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "fromEventId", fromEventId));
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

        ParameterizedTypeReference<EventsDTO> returnType = new ParameterizedTypeReference<EventsDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Read an event
     * 
     * <p><b>200</b> - The event corresponding to the provided \&quot;eventId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param eventId The unique identifier of an event (required)
     * @return EventDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public EventDTO ledgerEventsEventIdGet(Integer eventId) throws RestClientException {
        return ledgerEventsEventIdGetWithHttpInfo(eventId).getBody();
    }

    /**
     * Read an event
     * 
     * <p><b>200</b> - The event corresponding to the provided \&quot;eventId\&quot;
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>404</b> - Resource not found
     * <p><b>500</b> - An error occurred
     * @param eventId The unique identifier of an event (required)
     * @return ResponseEntity&lt;EventDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<EventDTO> ledgerEventsEventIdGetWithHttpInfo(Integer eventId) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'eventId' is set
        if (eventId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'eventId' when calling ledgerEventsEventIdGet");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("eventId", eventId);
        String path = UriComponentsBuilder.fromPath("/ledger/events/{eventId}").buildAndExpand(uriVariables).toUriString();
        
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

        ParameterizedTypeReference<EventDTO> returnType = new ParameterizedTypeReference<EventDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * Read collection of events. With no parameters returns events since start of current day.
     * 
     * <p><b>200</b> - List of events
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @param eventType Filter by event type (optional)
     * @param fromEventId  (optional)
     * @param since Events (since) timestamp UTC (optional)
     * @param before Events (before) timestamp UTC (optional)
     * @param size Number of events to be returned. Can be used to facilitate paging. Defaults to 100 (optional)
     * @return EventsDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public EventsDTO ledgerEventsGet(List<EventTypeDTO> eventType, Integer fromEventId, LocalDateTime since, LocalDateTime before, Integer size) throws RestClientException {
        return ledgerEventsGetWithHttpInfo(eventType, fromEventId, since, before, size).getBody();
    }

    /**
     * Read collection of events. With no parameters returns events since start of current day.
     * 
     * <p><b>200</b> - List of events
     * <p><b>401</b> - Not authorized to do this operation
     * <p><b>500</b> - An error occurred
     * @param eventType Filter by event type (optional)
     * @param fromEventId  (optional)
     * @param since Events (since) timestamp UTC (optional)
     * @param before Events (before) timestamp UTC (optional)
     * @param size Number of events to be returned. Can be used to facilitate paging. Defaults to 100 (optional)
     * @return ResponseEntity&lt;EventsDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<EventsDTO> ledgerEventsGetWithHttpInfo(List<EventTypeDTO> eventType, Integer fromEventId, LocalDateTime since, LocalDateTime before, Integer size) throws RestClientException {
        Object postBody = null;
        String path = UriComponentsBuilder.fromPath("/ledger/events").build().toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
        queryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("multi".toUpperCase()), "eventType", eventType));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "fromEventId", fromEventId));
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

        ParameterizedTypeReference<EventsDTO> returnType = new ParameterizedTypeReference<EventsDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
}
