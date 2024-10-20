package com.intellecteu.onesource.integration.api.services.declineinstructions;

import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface DeclineInstructionApiRepository extends JpaRepository<DeclineInstructionEntity, String>,
    JpaSpecificationExecutor<DeclineInstructionEntity> {

    boolean existsByRelatedExceptionEventId(String eventId);

}
