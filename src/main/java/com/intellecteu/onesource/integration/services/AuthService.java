package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.model.AuthToken;

public interface AuthService {

    /**
     * Retrieves an authorization token from an authorization server.
     *
     * @return AuthToken
     */
    AuthToken getAccessToken();

    /**
     * Retrieves an authorization token using refresh token.
     *
     * @param refreshToken String
     * @return AuthToken
     */
    AuthToken refreshToken(String refreshToken);

}
