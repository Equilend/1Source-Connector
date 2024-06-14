package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.entity.backoffice.ReturnTradeEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnTradeRepository extends JpaRepository<ReturnTradeEntity, Long> {

    Optional<ReturnTradeEntity> findTopByOrderByTradeIdDesc();

    List<ReturnTradeEntity> findAllByRelatedContractIdAndProcessingStatusAndMatching1SourceReturnIdNull(
        String relatedContractId, ProcessingStatus processingStatus);

    default List<ReturnTradeEntity> findUnmatchedReturnTradeEntity(
        String relatedContractId, ProcessingStatus processingStatus) {
        return findAllByRelatedContractIdAndProcessingStatusAndMatching1SourceReturnIdNull(
            relatedContractId, processingStatus);
    }

    Optional<ReturnTradeEntity> findByTradeIdAndProcessingStatusAndMatching1SourceReturnIdNull(Long tradeId,
        ProcessingStatus processingStatus);

    List<ReturnTradeEntity> findByRelatedPositionIdAndProcessingStatusIn(Long relatedPositionId,
        Collection<ProcessingStatus> processingStatuses);

    List<ReturnTradeEntity> findByTradeOut_StatusIn(Collection<String> statuses);

    ;
}
