package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.DtoTestFactory.createPartyDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.Participant;
import com.intellecteu.onesource.integration.model.ParticipantHolder;
import com.intellecteu.onesource.integration.repository.AgreementRepository;
import com.intellecteu.onesource.integration.repository.ContractRepository;
import com.intellecteu.onesource.integration.repository.ParticipantHolderRepository;
import com.intellecteu.onesource.integration.repository.PositionRepository;
import com.intellecteu.onesource.integration.repository.SettlementUpdateRepository;
import com.intellecteu.onesource.integration.repository.TimestampRepository;
import com.intellecteu.onesource.integration.repository.TradeEventRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class ParticipantFlowTests {

    private static final String ENDPOINT_FIELD_INJECT = "onesourceBaseEndpoint";
    private static final String LENDER_ENDPOINT_FIELD_INJECT = "lenderSpireEndpoint";
    private static final String BORROWER_ENDPOINT_FIELD_INJECT = "borrowerSpireEndpoint";
    private static final String VERSION_FIELD_INJECT = "version";
    private static final String TEST_ENDPOINT = "http://localhost:8080";
    private static final String TEST_API_VERSION = "/v1";
    private static final String TEST_PARTIES_ENDPOINT = "/ledger/parties";

    @Mock
    private SettlementUpdateRepository settlementUpdateRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private ContractRepository contractRepository;
    @Mock
    private TimestampRepository timestampRepository;
    @Mock
    private ParticipantHolderRepository participantHolderRepository;
    @Mock
    private AgreementRepository agreementRepository;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private CloudEventRecordService cloudEventRecordService;
    @Mock
    private TradeEventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private PositionMapper positionMapper;

    private OneSourceApiService oneSourceService;
    private SpireApiService spireService;
    private TradeEventService eventService;

    @BeforeEach
    void setUp() {
        oneSourceService = new OneSourceApiService(contractRepository, cloudEventRecordService, restTemplate,
            settlementUpdateRepository, eventMapper, eventRepository);
        spireService = new SpireApiService(restTemplate, positionRepository, eventMapper, settlementUpdateRepository,
            positionMapper, cloudEventRecordService);
        ReflectionTestUtils.setField(spireService, LENDER_ENDPOINT_FIELD_INJECT, TEST_ENDPOINT);
        ReflectionTestUtils.setField(spireService, BORROWER_ENDPOINT_FIELD_INJECT, TEST_ENDPOINT);
        ReflectionTestUtils.setField(oneSourceService, ENDPOINT_FIELD_INJECT, TEST_ENDPOINT);
        ReflectionTestUtils.setField(oneSourceService, VERSION_FIELD_INJECT, TEST_API_VERSION);
        eventService = new TradeEventService(eventRepository, agreementRepository, contractRepository,
            positionRepository, timestampRepository, participantHolderRepository, eventMapper, spireService,
            oneSourceService, cloudEventRecordService);
    }

    @Test
    @DisplayName("Parties were successfully retrieved first time with 200 response code.")
    void test_participantFlow_shouldRetrievePartiesFirstTime_success() {
        var partyDto = createPartyDto("test");

        var partiesUrl = TEST_ENDPOINT + TEST_API_VERSION + TEST_PARTIES_ENDPOINT;
        final ResponseEntity<List<PartyDto>> response = ResponseEntity.ok(List.of(partyDto));

        when(restTemplate.exchange(eq(partiesUrl), eq(GET), eq(null),
            eq(new ParameterizedTypeReference<List<PartyDto>>() {
            }))).thenReturn(response);
        when(participantHolderRepository.findAll()).thenReturn(new ArrayList<>());
        when(participantHolderRepository.save(any())).thenReturn(new ParticipantHolder());

        eventService.processParties();

        verify(participantHolderRepository).findAll();
        verify(participantHolderRepository).save(any());
        verify(restTemplate).exchange(eq(partiesUrl), eq(GET), eq(null),
            eq(new ParameterizedTypeReference<List<PartyDto>>() {
            }));
    }

    @Test
    @DisplayName("Parties were successfully retrieved and no update needed")
    void test_participantFlow_shouldRetrieveParties_noUpdateNeeded_success() {
        var partyDto = createPartyDto("test");
        Participant participant = Participant.builder()
            .gleifLei(partyDto.getGleifLei())
            .participantStartDate(LocalDateTime.now())
            .participantEndDate(null)
            .internalPartyId(partyDto.getInternalPartyId())
            .partyId(partyDto.getPartyId())
            .partyName(partyDto.getPartyName())
            .build();
        ParticipantHolder participantHolder = ParticipantHolder.builder()
            .participants(List.of(participant))
            .build();

        var partiesUrl = TEST_ENDPOINT + TEST_API_VERSION + TEST_PARTIES_ENDPOINT;
        final ResponseEntity<List<PartyDto>> response = ResponseEntity.ok(List.of(partyDto));

        when(restTemplate.exchange(eq(partiesUrl), eq(GET), eq(null),
            eq(new ParameterizedTypeReference<List<PartyDto>>() {
            }))).thenReturn(response);
        when(participantHolderRepository.findAll()).thenReturn(List.of(participantHolder));

        eventService.processParties();

        verify(restTemplate).exchange(eq(partiesUrl), eq(GET), eq(null),
            eq(new ParameterizedTypeReference<List<PartyDto>>() {
            }));
        verify(participantHolderRepository).findAll();
        verify(participantHolderRepository).save(any());
    }
}
