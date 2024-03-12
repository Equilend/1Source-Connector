package com.intellecteu.onesource.integration.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellecteu.onesource.integration.exception.RequiredDataMissedException;
import com.intellecteu.onesource.integration.exception.ValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExceptionUtilsTest {

    @Test
    void testNullField_shouldThrowException() {
        final ValidationException exception = assertThrows(
            ValidationException.class,
            () -> ExceptionUtils.throwIfFieldMissedException(null, "test")
        );

        assertEquals(List.of("test"), exception.getInvalidFields());
    }

}