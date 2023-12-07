package com.intellecteu.onesource.integration.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellecteu.onesource.integration.exception.RequiredDataMissedException;
import org.junit.jupiter.api.Test;

class ExceptionUtilsTest {

    @Test
    void testNullField_shouldThrowException() {
        final RequiredDataMissedException exception = assertThrows(
            RequiredDataMissedException.class,
            () -> ExceptionUtils.throwIfFieldMissedException(null, "test")
        );

        assertEquals("test", exception.getDto().getValue());
    }

}