package com.intellecteu.onesource.integration.services.client.spire;

import com.intellecteu.onesource.integration.services.client.spire.dto.LoanTradeInputDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryRequest;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponsePositionOutDTO;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponsePositionDTO;
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
import org.springframework.web.util.UriComponentsBuilder;

//@Component initiated in @see com.intellecteu.onesource.integration.config.AppConfig
public class PositionSpireApiClient {

    private ApiClient apiClient;
    private String clientId;

    public PositionSpireApiClient(ApiClient apiClient, String clientId) {
        this.apiClient = apiClient;
        this.clientId = clientId;
    }

    public ResponseEntity<SResponseNQueryResponsePositionOutDTO> getPositions(NQueryRequest nQueryRequest)
        throws HttpClientErrorException {
        ResponseEntity<SResponseNQueryResponsePositionOutDTO> response = getPositionsUsingPOSTWithHttpInfo(
            null, clientId, nQueryRequest);
        if (HttpStatus.CREATED.equals(response.getStatusCode())) {
            // temporal throw an exception to record until requirements will be retrieved how to handle 201 status
            throw new HttpClientErrorException(HttpStatus.CREATED);
        }
        return response;
    }

    public ResponseEntity<SResponsePositionDTO> editPosition(LoanTradeInputDTO input) throws HttpClientErrorException {
        ResponseEntity<SResponsePositionDTO> response = editPositionUsingPOSTWithHttpInfo(null, input);
        if (HttpStatus.CREATED.equals(response.getStatusCode())) {
            // temporal throw an exception to record until requirements will be retrieved how to handle 201 status
            throw new HttpClientErrorException(HttpStatus.CREATED);
        }
        return response;
    }

    /**
     * editPosition
     *
     * <p><b>200</b> - OK
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     *
     * @param accessToken Access token generated during user authentication (required)
     * @param input Values that can be edited on a Loan : positionType, positionTypeName, instructions,
     * positionReferenceNumber, exposureId, comments, counterpartyAccountNumber, allInRate, strategy (optional)
     * @return ResponseEntity&lt;SResponsePositionDTO&gt;
     * @throws HttpClientErrorException if an error occurs while attempting to invoke the API
     */
    private ResponseEntity<SResponsePositionDTO> editPositionUsingPOSTWithHttpInfo(String accessToken,
        LoanTradeInputDTO input) throws HttpClientErrorException {
        Object postBody = input;

        String path = UriComponentsBuilder.fromPath("/trades/editposition").build().toUriString();

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

        ParameterizedTypeReference<SResponsePositionDTO> returnType = new ParameterizedTypeReference<SResponsePositionDTO>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    /**
     * getPositions
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
     * description, counterpartyGroupAccountNo, counterpartyGroupAccountNo2 , counterpartyGroupName,
     * counterpartyAccountNo, counterpartyAccountNo2, counterpartyShortName, counterpartyLegalName, dtcNo, categoryName,
     * positionType, collateralType, poolPositionId, positionId, settleDate, depoGroup, depoKy, status, tradeDate,
     * resetDate, endDate, matchGroupName, accountGroupShortName, accountGroupLegalName, accountGroupAccountNo,
     * accountGroupAccountNo2, accountShortName, accountLegalName, accountNo, accountNo2, collateralAccountNo2,
     * collateralAccountNo, collateralShortName, collateralName, indexName, rate, spread, currencyKy, positionRef,
     * userName, nonMarkable, strategy, isStreetView, baseCurrencyKy, secCountryKy (optional)
     * @return ResponseEntity&lt;SResponseNQueryResponsePositionOutDTO&gt;
     * @throws HttpClientErrorException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<SResponseNQueryResponsePositionOutDTO> getPositionsUsingPOSTWithHttpInfo(String accessToken,
        String clientId, NQueryRequest nQueryRequest) throws HttpClientErrorException {
        Object postBody = nQueryRequest;

        String path = UriComponentsBuilder.fromPath("/trades/search/position/query").build().toUriString();

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

        ParameterizedTypeReference<SResponseNQueryResponsePositionOutDTO> returnType = new ParameterizedTypeReference<SResponseNQueryResponsePositionOutDTO>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }
}
