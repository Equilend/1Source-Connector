package com.intellecteu.onesource.integration.utils;

import static com.intellecteu.onesource.integration.exception.RequiredDataMissedException.REQUIRED_DATA_MISSED_MSG;

import com.intellecteu.onesource.integration.dto.ExceptionMessageDto;
import com.intellecteu.onesource.integration.exception.RequiredDataMissedException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

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
    public static void throwIfFieldMissedException(@Nullable Object field, String fieldName)
        throws RequiredDataMissedException {
        if (field == null) {
            var exceptionDto = new ExceptionMessageDto();
            exceptionDto.setValue(fieldName);
            exceptionDto.setExceptionMessage(String.format(REQUIRED_DATA_MISSED_MSG, exceptionDto.getValue()));
            log.debug("Validation failed. " + exceptionDto.getExceptionMessage());
            throw new RequiredDataMissedException(exceptionDto);
        }
    }

    public static void throwFieldMissedException(String fieldName) throws RequiredDataMissedException {
        throwIfFieldMissedException(null, fieldName);
    }
}
