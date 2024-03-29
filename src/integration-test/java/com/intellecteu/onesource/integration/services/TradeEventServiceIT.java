package com.intellecteu.onesource.integration.services;


import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.ContractStatus;
import com.intellecteu.onesource.integration.model.onesource.CurrencyCd;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.repository.entity.onesource.AgreementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.UnorderedRequestExpectationManager;
import org.springframework.web.client.RestTemplate;

@Disabled
public class TradeEventServiceIT extends AbstractTest {

    @Autowired
    TradeEventRepository tradeEventRepository;
    @Autowired
    ContractRepository contractRepository;
    @Autowired
    AgreementRepository agreementRepository;
    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${onesource.baseEndpoint}")
    private String baseEndpoint;
    @Value("${spire.baseSpireEndpoint}")
    private String baseSpireEndpoint;

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build(new UnorderedRequestExpectationManager());
    }

    @Test
    void getNewTradeEventsTest() throws IOException, URISyntaxException {
        mapper.registerModule(new JavaTimeModule());
        File eventFile = new File(getClass().getResource
            ("/tradeEvents.json").getFile());
        retrieveEvents(eventFile);

//        tradeEventService.retrieveNewEvents();
        List<TradeEventEntity> tradeEvents = tradeEventRepository.findAll();

        mockServer.verify();
        Assertions.assertEquals(tradeEvents.size(), 2);
        Assertions.assertEquals(tradeEvents.get(0).getEventType(), EventType.TRADE_AGREED);
        Assertions.assertEquals(tradeEvents.get(1).getEventType(), EventType.CONTRACT_PROPOSED);
        Assertions.assertEquals(tradeEvents.get(0).getEventDatetime(), LocalDateTime.parse("2023-07-27T13:41:29.011"));
        Assertions.assertEquals(tradeEvents.get(1).getEventDatetime(), LocalDateTime.parse("2023-07-27T13:41:29.011"));
    }

    @Test
    @Disabled
        // disabled until final implementation for Contract will be finished
    void getNewLoanContractTest() throws Exception {
        mapper.registerModule(new JavaTimeModule());
        mockServer.reset();

        File eventFile = new File(getClass().getResource
            ("/contractEvents.json").getFile());
        retrieveEvents(eventFile);

        File contractFile = new File(getClass().getResource
            ("/contracts.json").getFile());
        Contract contract = mapper.readValue(contractFile, Contract.class);
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(baseEndpoint.concat("ledger/contracts/32b71278-9ad2-445a-bfb0-b5ada72f7195"))))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(contract)));

//        tradeEventService.retrieveNewEvents();

//        tradeEventService.processEventData();

        List<ContractEntity> contracts = contractRepository.findAll();

        mockServer.verify();
        Assertions.assertEquals(contracts.size(), 1);
        Assertions.assertEquals(contracts.get(0).getContractId(), "32b71278-9ad2-445a-bfb0-b5ada72f7195");
        Assertions.assertEquals(contracts.get(0).getContractStatus(), ContractStatus.PROPOSED);
        Assertions.assertEquals(contracts.get(0).getProcessingStatus(), ProcessingStatus.PROCESSED);
        Assertions.assertEquals(contracts.get(0).getLastUpdateDateTime(),
            LocalDateTime.parse("2023-07-27T17:22:46.245"));
    }

    @Test
    @Disabled
        // disabled until final implementation for Trade Agreement will be finished
    void getTradeAgreementTest() throws Exception {
        mapper.registerModule(new JavaTimeModule());
        mockServer.reset();

        File eventFile = new File(getClass().getResource
            ("/agreementEvents.json").getFile());
        retrieveEvents(eventFile);

        File agreementFile = new File(getClass().getResource
            ("/agreements.json").getFile());
        AgreementDto agreement = mapper.readValue(agreementFile, AgreementDto.class);
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(baseEndpoint.concat("ledger/agreements/32b71278-9ad2-445a-bfb0-b5ada72f7193"))))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(agreement)));

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(baseSpireEndpoint.concat("/trades/search/position/query"))))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(""));

//        tradeEventService.retrieveNewEvents();

//        tradeEventService.processEventData();

        List<AgreementEntity> trades = agreementRepository.findAll();

        mockServer.verify();
        Assertions.assertEquals(trades.size(), 1);
        Assertions.assertEquals(trades.get(0).getAgreementId(), "32b71278-9ad2-445a-bfb0-b5ada72f7193");
        Assertions.assertEquals(trades.get(0).getTrade().getBillingCurrency(), CurrencyCd.USD);
        Assertions.assertEquals(trades.get(0).getTrade().getProcessingStatus(), ProcessingStatus.PROCESSED);
        Assertions.assertEquals(trades.get(0).getLastUpdateDatetime(),
            LocalDateTime.parse("2023-08-22T18:19:07.906"));
    }

    private void retrieveEvents(File file) throws IOException, URISyntaxException {
        List<TradeEventDto> tradeEventDtos = mapper.readValue(file, List.class);
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(baseEndpoint.concat(
                    "ledger/events?since=2023-06-25T09:51:16.111&eventType=TRADE&eventType=CONTRACT&eventType=CONTRACT_APPROVE&eventType=CONTRACT_CANCEL&eventType=CONTRACT_DECLINE"))))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(tradeEventDtos)));
    }
}
