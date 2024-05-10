package com.intellecteu.onesource.integration.routes.returns.processor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.openMocks;

import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ReturnTradeService;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventFactory;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import com.intellecteu.onesource.integration.services.systemevent.ReturnCloudEventBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ReturnProcessorTest {

    private ReturnProcessor returnProcessor;
    @Mock
    private BackOfficeService backOfficeService;
    @Mock
    private ReturnTradeService returnTradeService;
    @Mock
    private CloudEventRecordService cloudEventRecordService;
    @Mock
    private ReturnCloudEventBuilder eventBuilder;
    @BeforeEach
    void setUp() {
        openMocks(this);
        CloudEventFactory cloudEventFactory = mock(CloudEventFactory.class);
        doReturn(eventBuilder).when(cloudEventFactory).eventBuilder(any());
        doReturn(cloudEventFactory).when(cloudEventRecordService).getFactory();
        returnProcessor = new ReturnProcessor(backOfficeService, returnTradeService, cloudEventRecordService);
    }

    @Test
    void fetchNewReturnTrades_BackOfficeResponseWithModel_StoredEntity() {
        ReturnTrade returnTrade = new ReturnTrade();
        returnTrade.setTradeId(1l);
        doReturn(List.of(returnTrade)).when(backOfficeService).retrieveReturnTrades(any());

        List<ReturnTrade> result = returnProcessor.fetchNewReturnTrades();

        assertTrue(result.contains(returnTrade));
    }
}