package com.intellecteu.onesource.integration.api.declineinstructions;

import com.intellecteu.onesource.integration.repository.entity.toolkit.DeclineInstructionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface DeclineInstructionRepositoryApi extends JpaRepository<DeclineInstructionEntity, String>,
    JpaSpecificationExecutor<DeclineInstructionEntity> {

}
