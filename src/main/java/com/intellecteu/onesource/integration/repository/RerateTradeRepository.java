package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RerateTradeEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RerateTradeRepository extends JpaRepository<RerateTradeEntity, Long> {

    Optional<RerateTradeEntity> findTopByOrderByTradeIdDesc();

    List<RerateTradeEntity> findByRelatedContractId(String contractId);

    @Query("SELECT rt FROM RerateTradeEntity rt WHERE rt.relatedContractId = :contractId AND rt.matchingRerateId IS null ")
    List<RerateTradeEntity> findUnmatchedRerateTrades(String contractId);

    List<RerateTradeEntity> findByRelatedPositionIdAndProcessingStatusIn(Long relatedPositionId,
        Collection<ProcessingStatus> processingStatuses);
}
