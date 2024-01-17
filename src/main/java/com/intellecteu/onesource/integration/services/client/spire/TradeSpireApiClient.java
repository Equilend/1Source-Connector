package com.intellecteu.onesource.integration.services.client.spire;

import com.intellecteu.onesource.integration.services.client.spire.dto.NQueryRequest;
import com.intellecteu.onesource.integration.services.client.spire.dto.SResponseNQueryResponseTradeOutDTO;
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

        ResponseEntity<SResponseNQueryResponseTradeOutDTO> response = getTradesUsingPOSTWithHttpInfo(
            null, clientId, nQueryRequest);
        if (HttpStatus.CREATED.equals(response.getStatusCode())) {
            // temporal throw an exception to record until requirements will be retrieved how to handle 201 status
            throw new HttpClientErrorException(HttpStatus.CREATED);
        }
        return response;
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

        String path = UriComponentsBuilder.fromPath("/trades/search/trade/query").build().toUriString();

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

        ParameterizedTypeReference<SResponseNQueryResponseTradeOutDTO> returnType = new ParameterizedTypeReference<SResponseNQueryResponseTradeOutDTO>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

}
