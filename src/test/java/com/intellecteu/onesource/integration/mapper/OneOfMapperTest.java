package com.intellecteu.onesource.integration.mapper;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.config.AppConfig;
import com.intellecteu.onesource.integration.config.RestTemplateAuthInterceptor;
import com.intellecteu.onesource.integration.services.client.onesource.dto.FixedRateDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RebateRateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class OneOfMapperTest {

    @Mock
    private RestTemplateAuthInterceptor interceptor;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        openMocks(this);
        AppConfig appConfig = new AppConfig(interceptor);
        objectMapper = appConfig.objectMapper();
    }

    @Test
    /*
     * On failed test check the OneOf... interface in DTO and remove type or
     * use JsonTypeInfo.Id.DEDUCTION and assign required @JsonSubTypes for OneOf... implementations
     */
    public void testOneOfInterface_jsonWithInterfaceType_javaObjectWithTypeImpl() throws JsonProcessingException {
        String json = """
            {
              "rebate": {
                            "fixed": {
                              "baseRate": 0.00,
                              "effectiveRate": 0.00,
                              "effectiveDate": "2024-03-27",
                              "cutoffTime": "cutoffTime_ab2ebe23adc3"
                            }
                          }
            }
            """;

        RebateRateDTO rebateRateDTO = objectMapper.readValue(json, RebateRateDTO.class);

        assertTrue("change in all DTO interfaces JsonTypeInfo to use = JsonTypeInfo.Id.DEDUCTION", rebateRateDTO.getRebate() instanceof FixedRateDTO);
    }

}
