package com.intellecteu.onesource.integration.exception;

import java.util.List;

public class DataMismatchException extends ValidationException {

    public static final String DATA_MISMATCH_MSG = "Required data mismatch! Value: [%s] should be: [%s]";
    public static final String LEI_MISMATCH_MSG = "Transaction parties do not have matches with position lei: [%s]";

    public DataMismatchException() {
    }

    public DataMismatchException(List<String> invalidFields) {
        super(invalidFields);
    }

}
