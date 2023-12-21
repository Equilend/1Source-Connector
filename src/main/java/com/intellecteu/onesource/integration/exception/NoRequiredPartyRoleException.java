package com.intellecteu.onesource.integration.exception;

public class NoRequiredPartyRoleException extends RuntimeException {

    public static final String NO_PARTY_ROLE_EXCEPTION = """
        Couldn't retrieve party role from position %s""";

    public NoRequiredPartyRoleException() {
        super();
    }

    public NoRequiredPartyRoleException(String message) {
        super(message);
    }

}
