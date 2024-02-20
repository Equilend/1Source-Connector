package com.intellecteu.onesource.integration.api.services.cloudevents;

import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.DATA_TABLE;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.ID;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.RELATED_OBJECTS_TABLE;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.RELATED_OBJECT_ID;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.RELATED_OBJECT_TYPE;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.RELATED_PROCESS;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.RELATED_SUBPROCESS;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.SOURCE;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.SUBJECT;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.TIME;
import static com.intellecteu.onesource.integration.api.services.cloudevents.CloudSystemEventConstants.TYPE;

import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
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

    static Specification<CloudSystemEventEntity> sinceTime(LocalDate timeValue) {
        return (root, query, builder) ->
            timeValue == null ?
                builder.conjunction() :
                builder.greaterThanOrEqualTo(root.get(TIME), timeValue);
    }

    static Specification<CloudSystemEventEntity> beforeTime(LocalDate timeValue) {
        return (root, query, builder) ->
            timeValue == null ?
                builder.conjunction() :
                builder.lessThan(root.get(TIME), timeValue);
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

    static Specification<CloudSystemEventEntity> cloudEventRelatedObjectIdEquals(String relatedObjectId) {
        return (root, query, builder) ->
            relatedObjectId == null ?
                builder.conjunction() :
                builder.equal(root
                        .join(DATA_TABLE)
                        .join(RELATED_OBJECTS_TABLE)
                        .get(RELATED_OBJECT_ID),
                    relatedObjectId);
    }

    static Specification<CloudSystemEventEntity> cloudEventRelatedObjectTypeEquals(String relatedObjectType) {
        return (root, query, builder) ->
            relatedObjectType == null ?
                builder.conjunction() :
                builder.equal(root
                        .join(DATA_TABLE)
                        .join(RELATED_OBJECTS_TABLE)
                        .get(RELATED_OBJECT_TYPE),
                    relatedObjectType);
    }

}
