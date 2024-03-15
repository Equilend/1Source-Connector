package com.intellecteu.onesource.integration.api.services.cloudevents;

import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.BEFORE_TIME;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.ID;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.RELATED_OBJECT_ID;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.RELATED_OBJECT_TYPE;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.RELATED_PROCESS;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.RELATED_SUBPROCESS;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.SINCE_TIME;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.SOURCE;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.SUBJECT;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.TYPE;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventSpecs.beforeTime;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventSpecs.cloudEventIdEquals;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventSpecs.cloudEventRelatedObjectIdEquals;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventSpecs.cloudEventRelatedObjectTypeEquals;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventSpecs.cloudEventRelatedProcessEquals;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventSpecs.cloudEventRelatedSubProcessEquals;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventSpecs.cloudEventSourceEquals;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventSpecs.cloudEventSubjectEquals;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventSpecs.cloudEventTypeEquals;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventSpecs.sinceTime;

import com.intellecteu.onesource.integration.api.dto.CloudSystemEventDto;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CloudSystemEventService {

    private final CloudSystemEventRepositoryApi cloudEventRepository;
    private final CloudSystemEventApiMapper mapper;

    public PageResponse<CloudSystemEventDto> getCloudEvents(Pageable pageable,
        MultiValueMap<String, String> parameters) {
        Specification<CloudSystemEventEntity> dynamicFieldsFilter = createSpecificationForCloudEvents(parameters);
        final Page<CloudSystemEventEntity> events = cloudEventRepository.findAll(dynamicFieldsFilter, pageable);
        if (!parameters.isEmpty() && events.isEmpty()) {
            throw new EntityNotFoundException();
        }
        log.debug("Found {} cloud events", events.getTotalElements());
        final Page<CloudSystemEventDto> pageEvents = events.map(this::mapEvent);
        return PageResponse.<CloudSystemEventDto>builder()
            .totalItems(pageEvents.getTotalElements())
            .currentPage(pageEvents.getPageable().getPageNumber())
            .totalPages(pageEvents.getTotalPages())
            .items(pageEvents.getContent())
            .build();
    }

    public CloudSystemEventDto getCloudEventById(@NonNull String id) {
        final Optional<CloudSystemEventEntity> event = cloudEventRepository.findById(id);
        return event.map(this::mapEvent).orElseThrow(() -> new EntityNotFoundException(
            "The System event referred in decline instruction is not found"));
    }

    public LocalDateTime getLatestDateBySubject(@NonNull String subject) {
        return cloudEventRepository.findFirstBySubjectOrderByTimeDesc(subject)
            .map(CloudSystemEventEntity::getTime)
            .orElseThrow(() -> new EntityNotFoundException(
                "The System event referred in decline instruction is not found"));
    }

    private CloudSystemEventDto mapEvent(CloudSystemEventEntity entity) {
        try {
            return mapper.toCloudEventDto(entity);
        } catch (IllegalArgumentException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Specification<CloudSystemEventEntity> createSpecificationForCloudEvents(
        MultiValueMap<String, String> parameters) {
        String id = parameters.getFirst(ID);
        String type = parameters.getFirst(TYPE);
        String source = parameters.getFirst(SOURCE);
        String subject = parameters.getFirst(SUBJECT);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String sinceTime = parameters.getFirst(SINCE_TIME);
        LocalDate sinceTimeDate = null;
        if (sinceTime != null) {
            sinceTimeDate = LocalDate.parse(sinceTime, df);
        }

        String beforeTime = parameters.getFirst(BEFORE_TIME);
        LocalDate beforeTimeDate = null;
        if (beforeTime != null) {
            beforeTimeDate = LocalDate.parse(beforeTime, df);
        }

        String relatedProcess = parameters.getFirst(RELATED_PROCESS);
        String relatedSubProcess = parameters.getFirst(RELATED_SUBPROCESS);
        String relatedObjectId = parameters.getFirst(RELATED_OBJECT_ID);
        String relatedObjectType = parameters.getFirst(RELATED_OBJECT_TYPE);
        Specification<CloudSystemEventEntity> idSpec = cloudEventIdEquals(id);
        Specification<CloudSystemEventEntity> typeSpec = cloudEventTypeEquals(type);
        Specification<CloudSystemEventEntity> sourceSpec = cloudEventSourceEquals(source);
        Specification<CloudSystemEventEntity> subjectSpec = cloudEventSubjectEquals(subject);
        Specification<CloudSystemEventEntity> sinceTimeSpec = sinceTime(sinceTimeDate);
        Specification<CloudSystemEventEntity> beforeTimeSpec = beforeTime(beforeTimeDate);
        Specification<CloudSystemEventEntity> relatedProcessSpec = cloudEventRelatedProcessEquals(relatedProcess);
        Specification<CloudSystemEventEntity> relatedSubProcessSpec = cloudEventRelatedSubProcessEquals(
            relatedSubProcess);
        Specification<CloudSystemEventEntity> relatedObjectIdSpec = cloudEventRelatedObjectIdEquals(relatedObjectId);
        Specification<CloudSystemEventEntity> relatedObjectTypeSpec = cloudEventRelatedObjectTypeEquals(
            relatedObjectType);

        return Specification
            .where(idSpec)
            .and(typeSpec)
            .and(sourceSpec)
            .and(subjectSpec)
            .and(sinceTimeSpec)
            .and(beforeTimeSpec)
            .and(relatedProcessSpec)
            .and(relatedSubProcessSpec)
            .and(relatedObjectIdSpec)
            .and(relatedObjectTypeSpec);
    }
}
