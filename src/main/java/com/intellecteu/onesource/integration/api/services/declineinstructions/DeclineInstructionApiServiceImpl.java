package com.intellecteu.onesource.integration.api.services.declineinstructions;

import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.BEFORE_CREATED;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.DECLINE_INSTRUCTION_ID;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.EXCEPTION_EVENT_ID;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.RELATED_PROPOSAL_ID;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.SINCE_CREATED;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.USER_ID;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_DISCREPANCIES;
import static com.intellecteu.onesource.integration.model.enums.RecordType.LOAN_CONTRACT_PROPOSAL_UNMATCHED;

import com.intellecteu.onesource.integration.api.dto.CloudSystemEventDto;
import com.intellecteu.onesource.integration.api.dto.DeclineInstructionDto;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventService;
import com.intellecteu.onesource.integration.api.services.contracts.ContractApiService;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
class DeclineInstructionApiServiceImpl implements DeclineInstructionApiService {

    private final DeclineInstructionApiRepository declineInstructionRepository;
    private final DeclineInstructionApiMapper declineInstructionApiMapper;
    private final ContractApiService contractApiService;
    private final CloudSystemEventService cloudSystemEventService;

    @Override
    public PageResponse<DeclineInstructionDto> getAllInstructions(Pageable pageable,
        MultiValueMap<String, String> parameters) {
        Specification<DeclineInstructionEntity> dynamicFilters = createFiltersForDeclineInstruction(parameters);
        Page<DeclineInstructionEntity> instructionsPage = declineInstructionRepository.findAll(dynamicFilters,
            pageable);
        if (!parameters.isEmpty() && instructionsPage.isEmpty()) {
            throw new EntityNotFoundException();
        }
        log.debug("Found {} decline instructions", instructionsPage.getTotalElements());
        Page<DeclineInstructionDto> instructions = instructionsPage.map(declineInstructionApiMapper::toDto);
        return PageResponse.<DeclineInstructionDto>builder()
            .totalItems(instructions.getTotalElements())
            .currentPage(instructions.getPageable().getPageNumber())
            .totalPages(instructions.getTotalPages())
            .items(instructions.getContent())
            .build();
    }

    @Override
    @Transactional
    public DeclineInstructionDto createDeclineInstruction(DeclineInstructionDto declineInstructionDto) {
        if (StringUtils.isBlank(declineInstructionDto.getDeclineInstructionId())) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Decline Instruction Id must be present.");
        }
        validateDeclineInstructionCreation(declineInstructionDto);
        return createInstruction(declineInstructionDto);
    }

    private void validateDeclineInstructionCreation(DeclineInstructionDto declineInstructionDto) {
        try {
            validateLoanProposalStatus(declineInstructionDto.getRelatedProposalId());
            validateInstructionIsNew(declineInstructionDto.getRelatedExceptionEventId());

            final CloudSystemEventDto capturedSystemEvent = cloudSystemEventService.getCloudEventById(
                declineInstructionDto.getRelatedExceptionEventId());

            validateSystemEventType(capturedSystemEvent.getType());
            validateRelatedSystemEventIsNotOutdated(capturedSystemEvent, declineInstructionDto);
        } catch (EntityNotFoundException | DeclineInstructionCreationException e) {
            log.debug("Catched expected exception on decline instruction creation: " + e.getMessage());
            throw (e);
        } catch (Exception e) {
            log.warn("Not expected exception on decline instruction creation. Details: " + e.getMessage());
            throw new RuntimeException("The decline instruction cannot be processed due to a technical error");
        }
    }

    /*
     * Validate if there are no system events with the same subject created after the decline instruction creation
     * datetime.
     *
     * @throws DeclineInstructionCreationException if a new cloud event was created with the same subject
     */
    private void validateRelatedSystemEventIsNotOutdated(CloudSystemEventDto capturedEvent,
        DeclineInstructionDto declineInstructionDto) throws DeclineInstructionCreationException {
        final String subject = capturedEvent.getSubject();
        final LocalDateTime latestEventDate = cloudSystemEventService.getLatestDateBySubject(subject);
        if (latestEventDate.isAfter(declineInstructionDto.getCreationDateTime())) {
            throw new DeclineInstructionCreationException("The CloudEvent is outdated");
        }
    }

    /*
     * Handle all exceptions that were not covered by the business requirements.
     */
    private DeclineInstructionDto createInstruction(DeclineInstructionDto declineInstructionDto) {
        try {
            DeclineInstructionEntity entity = declineInstructionApiMapper.toEntity(declineInstructionDto);
            DeclineInstructionEntity persisted = declineInstructionRepository.save(entity);
            log.debug("Decline instruction with id={} is created", persisted.getDeclineInstructionId());
            return declineInstructionApiMapper.toDto(persisted);
        } catch (Exception e) {
            log.warn("Not expected exception on decline instruction creation. Details: " + e.getMessage());
            throw new RuntimeException("The decline instruction cannot be processed due to a technical error");
        }
    }

    private void validateInstructionIsNew(String eventId) {
        if (declineInstructionRepository.existsByRelatedExceptionEventId(eventId)) {
            throw new DeclineInstructionCreationException("A decline has already been instructed for the CloudEvent");
        }
    }

    private void validateSystemEventType(String capturedType) {
        if (Objects.equals(LOAN_CONTRACT_PROPOSAL_DISCREPANCIES.name(), capturedType)
            || (Objects.equals(LOAN_CONTRACT_PROPOSAL_UNMATCHED.name(), capturedType))) {
            return;
        }
        throw new DeclineInstructionCreationException("CloudEvent not eligible to trigger a proposal decline");
    }

    /*
     * Validate related loan contract proposal. The processing status should be either DISCREPANCIES or UNMATCHED.
     *
     * @param contractId the related loan contract proposal to the decline instruction
     * @throws EntityNotFoundException if the related loan contract proposal is not found
     * @throws DeclineInstructionCreationException if the processing status is not equals DISCREPANCIES or UNMATCHED
     */
    private void validateLoanProposalStatus(String contractId)
        throws DeclineInstructionCreationException, EntityNotFoundException {
        final ProcessingStatus processingStatus = contractApiService.getProcessingStatusByContractId(contractId)
            .orElseThrow(() -> new EntityNotFoundException(
                "The Proposal referred in decline instruction is not found"));
        if (DISCREPANCIES != processingStatus && UNMATCHED != processingStatus) {
            throw new DeclineInstructionCreationException(
                "CloudEvent not eligible to trigger a proposal decline");
        }
    }

    private Specification<DeclineInstructionEntity> createFiltersForDeclineInstruction(
        MultiValueMap<String, String> parameters) {
        String declineInstructionId = parameters.getFirst(DECLINE_INSTRUCTION_ID);
        String relatedExceptionEventId = parameters.getFirst(EXCEPTION_EVENT_ID);
        String relatedProposalId = parameters.getFirst(RELATED_PROPOSAL_ID);
        String userId = parameters.getFirst(USER_ID);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String sinceCreated = parameters.getFirst(SINCE_CREATED);
        LocalDate sinceCreatedDate = sinceCreated == null ? null : LocalDate.parse(sinceCreated, dateFormatter);

        String beforeCreated = parameters.getFirst(BEFORE_CREATED);
        LocalDate beforeCreatedDate = beforeCreated == null ? null : LocalDate.parse(beforeCreated, dateFormatter);

        return Specification
            .where(DeclineInstructionSpecs.declineInstructionIdEquals(declineInstructionId))
            .and(DeclineInstructionSpecs.relatedExceptionEventEquals(relatedExceptionEventId))
            .and(DeclineInstructionSpecs.relatedProposalIdEquals(relatedProposalId))
            .and(DeclineInstructionSpecs.userIdEquals(userId))
            .and(DeclineInstructionSpecs.sinceCreationDate(sinceCreatedDate))
            .and(DeclineInstructionSpecs.beforeCreationDate(beforeCreatedDate));
    }
}

