package com.intellecteu.onesource.integration.api.services.declineinstructions;

import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.BEFORE_CREATED;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.DECLINE_INSTRUCTION_ID;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.EXCEPTION_EVENT_ID;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.RELATED_PROPOSAL_ID;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.SINCE_CREATED;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.USER_ID;

import com.intellecteu.onesource.integration.api.dto.DeclineInstructionDto;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Override
    @Transactional
    public DeclineInstructionDto createDeclineInstruction(DeclineInstructionDto declineInstructionDto) {
        if (StringUtils.isBlank(declineInstructionDto.getDeclineInstructionId())) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Decline Instruction Id is required.");
        }
        DeclineInstructionEntity entity = declineInstructionApiMapper.toEntity(declineInstructionDto);
        DeclineInstructionEntity persisted = declineInstructionRepository.save(entity);
        log.debug("Decline instruction with id={} is created", persisted.getDeclineInstructionId());
        return declineInstructionApiMapper.toDto(persisted);
    }
}
