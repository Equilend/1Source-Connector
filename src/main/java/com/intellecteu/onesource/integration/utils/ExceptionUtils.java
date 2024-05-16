package com.intellecteu.onesource.integration.utils;

import static com.intellecteu.onesource.integration.exception.RequiredDataMissedException.REQUIRED_DATA_MISSED_MSG;

import com.intellecteu.onesource.integration.exception.RequiredDataMissedException;
import com.intellecteu.onesource.integration.exception.ValidationException;
import com.intellecteu.onesource.integration.model.ProcessExceptionDetails;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.FieldSource;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * A utility class for common methods for exceptions.
 */
@UtilityClass
@Slf4j
public class ExceptionUtils {

    /**
     * Create a dto with a required field name of exception with the exception message and throw
     * RequiredDataMissedException.
     *
     * @param fieldName String a field name which raises the exception
     * @throws RequiredDataMissedException after the exception dto was configured
     */
    public static void throwIfFieldMissedException(@Nullable Object field, String fieldName, FieldSource fieldSource)
        throws ValidationException {
        if (field == null) {
            var exceptionDto = new ProcessExceptionDetails();
            exceptionDto.setSource(fieldSource);
            exceptionDto.setFieldName(fieldName);
            exceptionDto.setFieldValue(String.format(REQUIRED_DATA_MISSED_MSG, exceptionDto.getFieldName()));
            exceptionDto.setFieldExceptionType(FieldExceptionType.MISSING);
            log.debug("Validation failed. " + exceptionDto.getFieldValue());
            throw new ValidationException(List.of(fieldName));
        }
    }

    public static void throwFieldMissedException(String fieldName, FieldSource fieldSource) throws ValidationException {
        throwIfFieldMissedException(null, fieldName, fieldSource);
    }

    public static void throwExceptionForRedeliveryPolicy(HttpStatusCodeException codeException) throws HttpStatusCodeException {
        if (codeException.getStatusCode().is5xxServerError()) {
            throw codeException;
        }
    }
}
