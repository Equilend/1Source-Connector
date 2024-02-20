package com.intellecteu.onesource.integration.api.services.declineinstructions;

import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.CREATION_DATE;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.DECLINE_INSTRUCTION_ID;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.EXCEPTION_EVENT_ID;
import static com.intellecteu.onesource.integration.api.services.declineinstructions.DeclineInstructionFields.USER_ID;

import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
class DeclineInstructionSpecs {

    static Specification<DeclineInstructionEntity> declineInstructionIdEquals(String declineInstructionId) {
        return (root, query, builder) ->
            declineInstructionId == null ?
                builder.conjunction() :
                builder.equal(root.get(DECLINE_INSTRUCTION_ID), declineInstructionId);
    }

    static Specification<DeclineInstructionEntity> relatedExceptionEventEquals(String relatedExceptionEventId) {
        return (root, query, builder) ->
            relatedExceptionEventId == null ?
                builder.conjunction() :
                builder.equal(root.get(EXCEPTION_EVENT_ID), relatedExceptionEventId);
    }

    static Specification<DeclineInstructionEntity> relatedProposalIdEquals(String relatedProposalId) {
        return (root, query, builder) ->
            relatedProposalId == null ?
                builder.conjunction() :
                builder.equal(root.get(EXCEPTION_EVENT_ID), relatedProposalId);
    }

    static Specification<DeclineInstructionEntity> sinceCreationDate(LocalDate sinceCreationDate) {
        return (root, query, builder) ->
            sinceCreationDate == null ?
                builder.conjunction() :
                builder.greaterThanOrEqualTo(root.get(CREATION_DATE), sinceCreationDate);
    }

    static Specification<DeclineInstructionEntity> beforeCreationDate(LocalDate beforeCreationDate) {
        return (root, query, builder) ->
            beforeCreationDate == null ?
                builder.conjunction() :
                builder.lessThan(root.get(CREATION_DATE), beforeCreationDate);
    }

    static Specification<DeclineInstructionEntity> userIdEquals(String userId) {
        return (root, query, builder) ->
            userId == null ?
                builder.conjunction() :
                builder.equal(root.get(USER_ID), userId);
    }

}
