package com.intellecteu.onesource.integration.api.cloudevents;

import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.ID;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.RELATED_PROCESS;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.RELATED_SUBPROCESS;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.SOURCE;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.SUBJECT;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.TIME;
import static com.intellecteu.onesource.integration.api.cloudevents.CloudSystemEventConstants.TYPE;

import com.intellecteu.onesource.integration.api.entities.CloudSystemEventEntity;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
class CloudSystemEventSpecs {

    static Specification<CloudSystemEventEntity> cloudEventIdEquals(String id) {
        return (root, query, builder) ->
            id == null ?
                builder.conjunction() :
                builder.equal(root.get(ID), id);
    }

    static Specification<CloudSystemEventEntity> cloudEventTypeEquals(String typeValue) {
        return (root, query, builder) ->
            typeValue == null ?
                builder.conjunction() :
                builder.equal(root.get(TYPE), typeValue);
    }

    static Specification<CloudSystemEventEntity> cloudEventSourceEquals(String sourceValue) {
        return (root, query, builder) ->
            sourceValue == null ?
                builder.conjunction() :
                builder.equal(root.get(SOURCE), sourceValue);
    }

    static Specification<CloudSystemEventEntity> cloudEventSubjectEquals(String subjectValue) {
        return (root, query, builder) ->
            subjectValue == null ?
                builder.conjunction() :
                builder.equal(root.get(SUBJECT), subjectValue);
    }

    static Specification<CloudSystemEventEntity> cloudEventTimeAfter(LocalDate timeValue) {
        return (root, query, builder) ->
            timeValue == null ?
                builder.conjunction() :
                builder.greaterThanOrEqualTo(root.get(TIME), timeValue);
    }

    static Specification<CloudSystemEventEntity> cloudEventRelatedProcessEquals(String relatedProcessValue) {
        return (root, query, builder) ->
            relatedProcessValue == null ?
                builder.conjunction() :
                builder.equal(root.get(RELATED_PROCESS), relatedProcessValue);
    }

    static Specification<CloudSystemEventEntity> cloudEventRelatedSubProcessEquals(String relatedSubProcessValue) {
        return (root, query, builder) ->
            relatedSubProcessValue == null ?
                builder.conjunction() :
                builder.equal(root.get(RELATED_SUBPROCESS), relatedSubProcessValue);
    }

}
