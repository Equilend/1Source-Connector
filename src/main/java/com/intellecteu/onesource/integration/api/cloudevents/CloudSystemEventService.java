package com.intellecteu.onesource.integration.api.cloudevents;

import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.ID;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.RELATED_PROCESS;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.RELATED_SUBPROCESS;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.SOURCE;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.SUBJECT;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.TIME;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.TYPE;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventSpecs.cloudEventIdEquals;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventSpecs.cloudEventRelatedProcessEquals;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventSpecs.cloudEventRelatedSubProcessEquals;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventSpecs.cloudEventSourceEquals;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventSpecs.cloudEventSubjectEquals;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventSpecs.cloudEventTimeAfter;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventSpecs.cloudEventTypeEquals;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class CloudSystemEventService {

    private final CloudSystemEventRepositoryApi cloudEventRepository;
    private final CloudSystemEventMapper cloudSystemEventMapper;

    Page<CloudSystemEvent> getCloudEvents(Pageable pageable, MultiValueMap<String, String> parameters) {
        Specification<CloudSystemEventEntity> dynamicFieldsFilter = createSpecificationForCloudEvents(parameters);
        final Page<CloudSystemEventEntity> events = cloudEventRepository.findAll(dynamicFieldsFilter, pageable);
        log.debug("Found {} cloud events", events.getTotalElements());
        return events.map(cloudSystemEventMapper::toCloudEvent);
    }

    CloudSystemEvent getCloudEventById(@NonNull String id) {
        final Optional<CloudSystemEventEntity> event = cloudEventRepository.findById(id);
        return event.map(cloudSystemEventMapper::toCloudEvent).orElseThrow(EntityNotFoundException::new);
    }

    private Specification<CloudSystemEventEntity> createSpecificationForCloudEvents(
        MultiValueMap<String, String> parameters) {
        String id = parameters.getFirst(ID);
        String type = parameters.getFirst(TYPE);
        String source = parameters.getFirst(SOURCE);
        String subject = parameters.getFirst(SUBJECT);
        String time = parameters.getFirst(TIME);
        LocalDate date = null;
        if (time != null) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = LocalDate.parse(time, df);
        }
        String relatedProcess = parameters.getFirst(RELATED_PROCESS);
        String relatedSubProcess = parameters.getFirst(RELATED_SUBPROCESS);
//        String relatedobjectid = parameters.getFirst("relatedobjectid");
//        String relatedobjecttype = parameters.getFirst("relatedobjecttype");
        Specification<CloudSystemEventEntity> idSpec = cloudEventIdEquals(id);
        Specification<CloudSystemEventEntity> typeSpec = cloudEventTypeEquals(type);
        Specification<CloudSystemEventEntity> sourceSpec = cloudEventSourceEquals(source);
        Specification<CloudSystemEventEntity> subjectSpec = cloudEventSubjectEquals(subject);
        Specification<CloudSystemEventEntity> timeAfterSpec = cloudEventTimeAfter(date);
        Specification<CloudSystemEventEntity> relatedProcessSpec = cloudEventRelatedProcessEquals(relatedProcess);
        Specification<CloudSystemEventEntity> relatedSubProcessSpec = cloudEventRelatedSubProcessEquals(
            relatedSubProcess);

        return Specification
            .where(idSpec)
            .and(typeSpec)
            .and(sourceSpec)
            .and(subjectSpec)
            .and(timeAfterSpec)
            .and(relatedProcessSpec)
            .and(relatedSubProcessSpec);
    }
}
