package com.intellecteu.onesource.integration.exception;

import java.util.List;

public class RequiredDataMissedException extends ValidationException {

    public static final String REQUIRED_DATA_MISSED_MSG = "Required data: [%s] must not be null!";

    public RequiredDataMissedException() {
    }

    public RequiredDataMissedException(List<String> invalidFields) {
        super(invalidFields);
    }

}
