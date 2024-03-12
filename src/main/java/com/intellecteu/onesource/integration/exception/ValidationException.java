package com.intellecteu.onesource.integration.exception;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationException extends Exception {

    private List<String> invalidFields;

    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(List<String> invalidFields) {
        super(String.join(",", invalidFields));
        this.invalidFields = invalidFields;
    }

}
