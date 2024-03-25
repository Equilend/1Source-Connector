package com.intellecteu.onesource.integration.services.client.spire;

import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryRequest;
import com.intellecteu.onesource.integration.services.client.spire.dto.OneSourceConfimationDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponseTradeOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseOneSourceConfimationDTO;
import com.intellecteu.onesource.integration.services.client.spire.invoker.ApiClient;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

public class TradeSpireApiClient {

    private ApiClient apiClient;
    private String clientId;

    public TradeSpireApiClient(ApiClient apiClient, String clientId) {
        this.apiClient = apiClient;
        this.clientId = clientId;
    }

    public ResponseEntity<SResponseNQueryResponseTradeOutDTO> getTrades(NQueryRequest nQueryRequest)
        throws RestClientException {

        ResponseEntity<SResponseNQueryResponseTradeOutDTO> response = getOneSourceTradesUsingPOSTWithHttpInfo(
            null, clientId, nQueryRequest);
        if (HttpStatus.CREATED.equals(response.getStatusCode())) {
            // temporal throw an exception to record until requirements will be retrieved how to handle 201 status
            throw new HttpClientErrorException(HttpStatus.CREATED);
        }
        return response;
    }


    @Deprecated(since = "0.0.5-SNAPSHOT")
    public ResponseEntity<SResponseNQueryResponseTradeOutDTO> getTradesObsolete(NQueryRequest nQueryRequest)
        throws RestClientException {

        ResponseEntity<SResponseNQueryResponseTradeOutDTO> response = getTradesUsingPOSTWithHttpInfo(
            null, clientId, nQueryRequest);
        if (HttpStatus.CREATED.equals(response.getStatusCode())) {
            // temporal throw an exception to record until requirements will be retrieved how to handle 201 status
            throw new HttpClientErrorException(HttpStatus.CREATED);
        }
        return response;
    }

    /**
     * getOneSourceTrades
     *
     * <p><b>200</b> - OK
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     *
     * @param accessToken Access token generated during user authentication (required)
     * @param clientId client_id (optional)
     * @param nQueryRequest Allowed lValue of NQuery: sedol, isin, cusip, ticker, clientSecId, quickCode, primaryId,
     * tradeId, positionId, accountGroupName, accountGroupAccountNo, accountGroupAccountNo2, accountName, accountNo,
     * accountNo2, collateralName, collateralAccountNo, collateralAccountNo2, status, groupType, depoGroup, depoKy,
     * currencyKy, tradeType, productLine, counterpartyGroupName, counterpartyGroupAccountNo,
     * counterpartyGroupAccountNo2, counterpartyName, counterpartyShortName, counterpartyAccountNo,
     * counterpartyAccountNo2, dtcNo, positionRef, settleDate, postDate, positionType, settleStatus, userName,
     * isStreetView, isLate, baseCurrencyKy, secCountryKy (optional)
     * @return ResponseEntity&lt;SResponseNQueryResponseTradeOutDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<SResponseNQueryResponseTradeOutDTO> getOneSourceTradesUsingPOSTWithHttpInfo(
        String accessToken, String clientId, NQueryRequest nQueryRequest) throws RestClientException {
        Object postBody = nQueryRequest;

        String path = UriComponentsBuilder.fromPath("/trades/search/onesource/trade/query").build().toUriString();

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

        ParameterizedTypeReference<SResponseNQueryResponseTradeOutDTO> returnType = new ParameterizedTypeReference<>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    /**
     * getTrades
     *
     * <p><b>200</b> - OK
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     *
     * @param accessToken Access token generated during user authentication (required)
     * @param clientId client_id (optional)
     * @param nQueryRequest Allowed lValue of NQuery: sedol, isin, cusip, ticker, clientSecId, quickCode, primaryId,
     * tradeId, positionId, accountGroupName, accountGroupAccountNo, accountGroupAccountNo2, accountName, accountNo,
     * accountNo2, collateralName, collateralAccountNo, collateralAccountNo2, status, groupType, depoGroup, depoKy,
     * currencyKy, tradeType, productLine, counterpartyGroupName, counterpartyGroupAccountNo,
     * counterpartyGroupAccountNo2, counterpartyName, counterpartyShortName, counterpartyAccountNo,
     * counterpartyAccountNo2, dtcNo, positionRef, settleDate, postDate, positionType, settleStatus, userName,
     * isStreetView, isLate, baseCurrencyKy, secCountryKy (optional)
     * @return ResponseEntity&lt;SResponseNQueryResponseTradeOutDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<SResponseNQueryResponseTradeOutDTO> getTradesUsingPOSTWithHttpInfo(String accessToken,
        String clientId, NQueryRequest nQueryRequest) throws RestClientException {
        Object postBody = nQueryRequest;

        String path = UriComponentsBuilder.fromPath("/trades/search/onesource/trade/query").build().toUriString();

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

        ParameterizedTypeReference<SResponseNQueryResponseTradeOutDTO> returnType = new ParameterizedTypeReference<>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    /**
     * confirmAndBatchPendingPositions
     *
     * <p><b>200</b> - OK
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     *
     * @param accessToken Access token generated during user authentication (required)
     * @param input input (required)
     * @return SResponseOneSourceConfimationDTO
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public SResponseOneSourceConfimationDTO confirmAndBatchPendingPositionsUsingPOST(String accessToken,
        OneSourceConfimationDTO input) throws RestClientException {
        final ResponseEntity<SResponseOneSourceConfimationDTO> sResponseOneSourceConfimationDTOResponseEntity =
            confirmAndBatchPendingPositionsUsingPOSTWithHttpInfo(accessToken, input);
        if (sResponseOneSourceConfimationDTOResponseEntity.getStatusCode() == HttpStatus.CREATED) {
            throw new HttpClientErrorException(HttpStatus.CREATED); // temporal workaround
        }
        return sResponseOneSourceConfimationDTOResponseEntity.getBody();
    }

    /**
     * confirmAndBatchPendingPositions
     *
     * <p><b>200</b> - OK
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     *
     * @param accessToken Access token generated during user authentication (required)
     * @param input input (required)
     * @return ResponseEntity&lt;SResponseOneSourceConfimationDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<SResponseOneSourceConfimationDTO> confirmAndBatchPendingPositionsUsingPOSTWithHttpInfo(
        String accessToken, OneSourceConfimationDTO input) throws RestClientException {
        Object postBody = input;

        String path = UriComponentsBuilder.fromPath("/trades/oneSource/confirmation").build().toUriString();

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

        ParameterizedTypeReference<SResponseOneSourceConfimationDTO> returnType = new ParameterizedTypeReference<SResponseOneSourceConfimationDTO>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }


    public ResponseEntity<SResponseOneSourceConfimationDTO> confirmTrade(OneSourceConfimationDTO input){
        ResponseEntity<SResponseOneSourceConfimationDTO> response = confirmAndBatchPendingPositionsUsingPOSTWithHttpInfo(
            null, input);
        if (HttpStatus.CREATED.equals(response.getStatusCode())) {
            throw new HttpClientErrorException(HttpStatus.CREATED);
        }
        return response;
    }

    /**
     * confirmAndBatchPendingPositions
     *
     * <p><b>200</b> - OK
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param accessToken Access token generated during user authentication (required)
     * @param input input (required)
     * @return ResponseEntity&lt;SResponseOneSourceConfimationDTO&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<SResponseOneSourceConfimationDTO> confirmAndBatchPendingPositionsUsingPOSTWithHttpInfo(String accessToken, OneSourceConfimationDTO input) throws RestClientException {
        Object postBody = input;

        // verify the required parameter 'input' is set
        if (input == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'input' when calling confirmAndBatchPendingPositionsUsingPOST");
        }

        String path = UriComponentsBuilder.fromPath("/trades/oneSource/comfirmation").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (accessToken != null)
            headerParams.add("access_token", apiClient.parameterToString(accessToken));

        final String[] accepts = {
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {
            "application/json"
        };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<SResponseOneSourceConfimationDTO> returnType = new ParameterizedTypeReference<SResponseOneSourceConfimationDTO>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

}
