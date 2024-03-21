package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.entity.onesource.RerateEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RerateRepository extends JpaRepository<RerateEntity, String> {

    List<RerateEntity> findByContractIdAndProcessingStatus(String contractId, ProcessingStatus processingStatus);
}
