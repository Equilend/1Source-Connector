package com.intellecteu.onesource.integration.services.client.spire.invoker.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

@jakarta.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2024-01-07T18:39:54.052Z")
public class OAuth implements Authentication {

    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void applyToParams(MultiValueMap<String, String> queryParams, HttpHeaders headerParams) {
        if (accessToken != null) {
            headerParams.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        }
    }
}
