package com.intellecteu.onesource.integration.api.services.nackinstructions;

import com.intellecteu.onesource.integration.repository.entity.toolkit.NackInstructionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NackInstructionApiRepository extends PagingAndSortingRepository<NackInstructionEntity, String>,
    JpaSpecificationExecutor<NackInstructionEntity>, JpaRepository<NackInstructionEntity, String> {

}
