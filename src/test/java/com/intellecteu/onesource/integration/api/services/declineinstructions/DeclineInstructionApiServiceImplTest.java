package com.intellecteu.onesource.integration.api.services.declineinstructions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellecteu.onesource.integration.api.EntityApiTestFactory;
import com.intellecteu.onesource.integration.api.dto.CloudSystemEventDto;
import com.intellecteu.onesource.integration.api.dto.DeclineInstructionDto;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventService;
import com.intellecteu.onesource.integration.api.services.contracts.ContractApiService;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DeclineInstructionApiServiceImplTest {

    @Mock
    private DeclineInstructionApiRepository declineInstructionRepository;
    @Mock
    private ContractApiService contractApiService;
    @Mock
    private CloudSystemEventService cloudSystemEventService;

    private DeclineInstructionApiMapper mapper;

    private DeclineInstructionApiService service;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(DeclineInstructionApiMapper.class);
        service = new DeclineInstructionApiServiceImpl(declineInstructionRepository, mapper,
            contractApiService, cloudSystemEventService);
    }

    @Test
    @Order(1)
    @DisplayName("Get all decline instructions with declineInstructionId in path parameters")
    void testGetAllDeclineInstructionsWithDeclineInstructionIdInParameters_shouldReturnPageResponse() {
        String id = "declineInstructionId";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.put("declineInstructionId", List.of(id));

        Pageable pageable = PageRequest.of(0, 20);
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        List<DeclineInstructionEntity> entities = List.of(entity);
        Page<DeclineInstructionEntity> dbResponse = new PageImpl<>(entities, pageable, 20);

        final DeclineInstructionDto event = mapper.toDto(entity);
        List<DeclineInstructionDto> events = List.of(event);
        Page<DeclineInstructionDto> expectedResponse = new PageImpl<>(events, pageable, 20);

        when(declineInstructionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(dbResponse);

        final PageResponse<DeclineInstructionDto> actual = service.getAllInstructions(pageable, parameters);

        assertEquals(expectedResponse.getTotalElements(), actual.getTotalItems());
        assertEquals(expectedResponse.getTotalPages(), actual.getTotalPages());
        assertEquals(expectedResponse.getPageable().getPageNumber(), actual.getCurrentPage());
        assertEquals(expectedResponse.getContent(), actual.getItems());
    }

    @Test
    @Order(2)
    @DisplayName("Get all decline instructions without parameters")
    void testGetAllDeclineInstructionsWithoutParameters_shouldReturnPageResponse() {
        String id = "declineInstructionId";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        Pageable pageable = PageRequest.of(0, 20);
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        List<DeclineInstructionEntity> entities = List.of(entity);
        Page<DeclineInstructionEntity> dbResponse = new PageImpl<>(entities, pageable, 20);

        final DeclineInstructionDto event = mapper.toDto(entity);
        List<DeclineInstructionDto> events = List.of(event);
        Page<DeclineInstructionDto> expectedResponse = new PageImpl<>(events, pageable, 20);

        when(declineInstructionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(dbResponse);

        final PageResponse<DeclineInstructionDto> actual = service.getAllInstructions(pageable, parameters);

        assertEquals(expectedResponse.getTotalElements(), actual.getTotalItems());
        assertEquals(expectedResponse.getTotalPages(), actual.getTotalPages());
        assertEquals(expectedResponse.getPageable().getPageNumber(), actual.getCurrentPage());
        assertEquals(expectedResponse.getContent(), actual.getItems());
    }

    @Test
    @Order(3)
    @DisplayName("Create decline instruction when related proposal processing status is DISCREPANCIES")
    void testCreateDeclineInstruction_shouldAcceptDeclineInstruction_whenProposalHasDiscrepanciesProcessingStatus() {
        String id = "declineInstructionId";
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        final DeclineInstructionDto dto = mapper.toDto(entity);
        dto.setRelatedProposalId("12345");
        dto.setRelatedExceptionEventId("999");

        CloudSystemEventDto eventDto = CloudSystemEventDto.builder()
            .subject("testSubject")
            .type("LOAN_CONTRACT_PROPOSAL_DISCREPANCIES")
            .time(LocalDateTime.now().minusDays(1))
            .build();

        when(cloudSystemEventService.getCloudEventById(dto.getRelatedExceptionEventId()))
            .thenReturn(eventDto);
        when(declineInstructionRepository.save(any(DeclineInstructionEntity.class))).thenReturn(entity);
        when(contractApiService.getProcessingStatusByContractId(dto.getRelatedProposalId()))
            .thenReturn(Optional.of(ProcessingStatus.DISCREPANCIES));
        when(declineInstructionRepository.existsByRelatedExceptionEventId(dto.getRelatedExceptionEventId()))
            .thenReturn(false);
        when(cloudSystemEventService.getLatestDateBySubject("testSubject"))
            .thenReturn(LocalDateTime.now().minusMonths(2));

        service.createDeclineInstruction(dto);

        verify(contractApiService).getProcessingStatusByContractId("12345");
        verify(declineInstructionRepository).existsByRelatedExceptionEventId("999");
        verify(cloudSystemEventService).getCloudEventById("999");
        verify(cloudSystemEventService).getLatestDateBySubject("testSubject");
    }

    @Test
    @Order(4)
    @DisplayName("Create decline instruction when related proposal processing status is UNMATCHED")
    void testCreateDeclineInstruction_shouldAcceptDeclineInstruction_whenProposalHasUnmatchedProcessingStatus() {
        String id = "declineInstructionId";
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        final DeclineInstructionDto dto = mapper.toDto(entity);
        dto.setRelatedProposalId("12345");
        dto.setRelatedExceptionEventId("999");

        CloudSystemEventDto eventDto = CloudSystemEventDto.builder()
            .subject("testSubject")
            .type("LOAN_CONTRACT_PROPOSAL_DISCREPANCIES")
            .time(LocalDateTime.now().minusDays(1))
            .build();

        when(cloudSystemEventService.getCloudEventById(dto.getRelatedExceptionEventId()))
            .thenReturn(eventDto);
        when(declineInstructionRepository.save(any(DeclineInstructionEntity.class))).thenReturn(entity);
        when(contractApiService.getProcessingStatusByContractId(dto.getRelatedProposalId()))
            .thenReturn(Optional.of(ProcessingStatus.UNMATCHED));
        when(declineInstructionRepository.existsByRelatedExceptionEventId(dto.getRelatedExceptionEventId()))
            .thenReturn(false);
        when(cloudSystemEventService.getLatestDateBySubject("testSubject"))
            .thenReturn(LocalDateTime.now().minusMonths(2));

        service.createDeclineInstruction(dto);

        verify(contractApiService).getProcessingStatusByContractId("12345");
        verify(declineInstructionRepository).existsByRelatedExceptionEventId("999");
        verify(cloudSystemEventService).getCloudEventById("999");
        verify(cloudSystemEventService).getLatestDateBySubject("testSubject");
    }

    @Test
    @Order(5)
    @DisplayName("Throw exception when related proposal is not found")
    void testCreateDeclineInstruction_shouldThrowException_whenProposalIsNotFound() {
        String id = "declineInstructionId";
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        final DeclineInstructionDto dto = mapper.toDto(entity);
        dto.setRelatedProposalId("12345");
        dto.setRelatedExceptionEventId("999");

        when(contractApiService.getProcessingStatusByContractId(dto.getRelatedProposalId()))
            .thenReturn(Optional.empty());

        final EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> service.createDeclineInstruction(dto));

        assertEquals("The Proposal referred in decline instruction is not found", exception.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Throw exception when proposal processing status is not DISCREPANCIES or UNMATCHED")
    void testCreateDeclineInstruction_shouldThrowException_whenProposalInNotRequiredProcessingStatus() {
        String id = "declineInstructionId";
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        final DeclineInstructionDto dto = mapper.toDto(entity);
        dto.setRelatedProposalId("12345");
        dto.setRelatedExceptionEventId("999");

        when(contractApiService.getProcessingStatusByContractId(dto.getRelatedProposalId()))
            .thenReturn(Optional.of(ProcessingStatus.CREATED));

        final DeclineInstructionCreationException exception = assertThrows(
            DeclineInstructionCreationException.class,
            () -> service.createDeclineInstruction(dto));

        assertEquals("CloudEvent not eligible to trigger a proposal decline", exception.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Throw exception when related system event is not found")
    void testCreateDeclineInstruction_shouldThrowException_whenRelatedSystemEventIsNotFound() {
        String id = "declineInstructionId";
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        final DeclineInstructionDto dto = mapper.toDto(entity);

        when(contractApiService.getProcessingStatusByContractId(dto.getRelatedProposalId()))
            .thenReturn(Optional.of(ProcessingStatus.DISCREPANCIES));
        when(declineInstructionRepository.existsByRelatedExceptionEventId(dto.getRelatedExceptionEventId()))
            .thenReturn(false);
        when(cloudSystemEventService.getCloudEventById(dto.getRelatedExceptionEventId()))
            .thenThrow(new EntityNotFoundException("The System event referred in decline instruction is not found"));

        final EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> service.createDeclineInstruction(dto));

        assertEquals("The System event referred in decline instruction is not found", exception.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Throw exception when decline instruction already created for system event")
    void testCreateDeclineInstruction_shouldThrowException_whenDeclineInstructionIsAlreadyCreatedForSystemEvent() {
        String id = "declineInstructionId";
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        final DeclineInstructionDto dto = mapper.toDto(entity);

        when(contractApiService.getProcessingStatusByContractId(dto.getRelatedProposalId()))
            .thenReturn(Optional.of(ProcessingStatus.DISCREPANCIES));
        when(declineInstructionRepository.existsByRelatedExceptionEventId(dto.getRelatedExceptionEventId()))
            .thenReturn(true);

        final DeclineInstructionCreationException exception = assertThrows(
            DeclineInstructionCreationException.class,
            () -> service.createDeclineInstruction(dto));

        assertEquals("A decline has already been instructed for the CloudEvent", exception.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("Throw exception when related Cloud Event doesn't have required type")
    void testCreateDeclineInstruction_shouldThrowException_whenRelatedSystemEventHasImproperType() {
        String id = "declineInstructionId";
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        final DeclineInstructionDto dto = mapper.toDto(entity);

        CloudSystemEventDto eventDto = CloudSystemEventDto.builder()
            .type("WRONG_TYPE")
            .build();

        when(contractApiService.getProcessingStatusByContractId(dto.getRelatedProposalId()))
            .thenReturn(Optional.of(ProcessingStatus.UNMATCHED));
        when(declineInstructionRepository.existsByRelatedExceptionEventId(dto.getRelatedExceptionEventId()))
            .thenReturn(false);
        when(cloudSystemEventService.getCloudEventById(dto.getRelatedExceptionEventId()))
            .thenReturn(eventDto);

        final DeclineInstructionCreationException exception = assertThrows(
            DeclineInstructionCreationException.class,
            () -> service.createDeclineInstruction(dto));

        assertEquals("CloudEvent not eligible to trigger a proposal decline", exception.getMessage());
    }

    @Test
    @Order(10)
    @DisplayName("Throw exception when new related Cloud Event was created after decline instruction was sent")
    @Disabled("for demo only. Unexpected behavior")
    void testCreateDeclineInstruction_shouldThrowException_whenRelatedSystemEventWasCreatedAfterDeclineInstruction() {
        String id = "declineInstructionId";
        final DeclineInstructionEntity entity = EntityApiTestFactory.buildDeclineInstructionEntity(id);
        final DeclineInstructionDto dto = mapper.toDto(entity);

        CloudSystemEventDto outDatedEvent = CloudSystemEventDto.builder()
            .subject("testSubject")
            .type("LOAN_CONTRACT_PROPOSAL_DISCREPANCIES")
            .time(LocalDateTime.now().minusDays(2))
            .build();

        when(contractApiService.getProcessingStatusByContractId(dto.getRelatedProposalId()))
            .thenReturn(Optional.of(ProcessingStatus.UNMATCHED));
        when(declineInstructionRepository.existsByRelatedExceptionEventId(dto.getRelatedExceptionEventId()))
            .thenReturn(false);
        when(cloudSystemEventService.getCloudEventById(dto.getRelatedExceptionEventId()))
            .thenReturn(outDatedEvent);
        when(cloudSystemEventService.getLatestDateBySubject("testSubject"))
            .thenReturn(LocalDateTime.now());

        final DeclineInstructionCreationException exception = assertThrows(
            DeclineInstructionCreationException.class,
            () -> service.createDeclineInstruction(dto));

        assertEquals("The CloudEvent is outdated", exception.getMessage());
    }


}