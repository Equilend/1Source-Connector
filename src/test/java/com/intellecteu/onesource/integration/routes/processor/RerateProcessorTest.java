package com.intellecteu.onesource.integration.routes.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.openMocks;

import com.intellecteu.onesource.integration.model.spire.RerateTrade;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.ContractService;
import com.intellecteu.onesource.integration.services.RerateTradeService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RerateProcessorTest {

    @Mock
    private BackOfficeService lenderBackOfficeService;
    @Mock
    private BackOfficeService borrowerBackOfficeService;
    @Mock
    private ContractService contractService;
    @Mock
    private RerateTradeService rerateTradeService;

    RerateProcessor rerateProcessor;

    @BeforeEach
    void setUp() {
        openMocks(this);
        rerateProcessor = new RerateProcessor(lenderBackOfficeService, borrowerBackOfficeService, contractService,
            rerateTradeService);
    }

    @Test
    void fetchNewTradeOut_BackOfficeResponseWithModel_StoredEntity() {
        RerateTrade rerateTrade = new RerateTrade();
        rerateTrade.setTradeId(1l);
        List<RerateTrade> lenderRerateTradeList = List.of(rerateTrade);
        doReturn(lenderRerateTradeList).when(lenderBackOfficeService).getNewBackOfficeTradeEvents(any(), any());

        rerateProcessor.fetchNewTradeOut();

        Mockito.verify(rerateTradeService, times(1)).save(any());
    }
}