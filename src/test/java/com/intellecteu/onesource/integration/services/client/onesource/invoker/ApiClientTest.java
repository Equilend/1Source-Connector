package com.intellecteu.onesource.integration.services.client.onesource.invoker;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiClientTest {

    private ApiClient apiClient;

    @BeforeEach
    void setUp() {
        apiClient = new ApiClient();
    }

    @Test
    void parameterToString_LocalDataTimeWithZeroSec_StringWithSec() {
        LocalDateTime localDateTimeWithZeroSec = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0);

        String result = apiClient.parameterToString(localDateTimeWithZeroSec);

        assertEquals("2020-01-01T00:00:00Z", result);
    }
}