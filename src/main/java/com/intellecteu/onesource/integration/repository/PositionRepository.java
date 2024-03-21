package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> {

    List<PositionEntity> findAll();

    Optional<PositionEntity> getByPositionId(Long positionId);

    @Query("select p from PositionEntity p where p.matching1SourceLoanContractId is null "
        + "and (p.processingStatus = 'CREATED' or p.processingStatus = 'UPDATED')"
        + "and p.positionId = :positionId")
    Optional<PositionEntity> getNotMatchedByPositionId(Long positionId);

    @Query("select p from PositionEntity p where p.matching1SourceLoanContractId is null "
        + "and (p.processingStatus = 'CREATED' or p.processingStatus = 'UPDATED')")
    Optional<PositionEntity> getNotMatched();

    @Query("select p from PositionEntity p where p.processingStatus <> 'CANCELED' and p.processingStatus <> 'SETTLED'")
    List<PositionEntity> findAllNotCanceledAndSettled();

    Optional<PositionEntity> findByVenueRefId(String venueRefId);

    Optional<PositionEntity> findByCustomValue2(String venueRefId);

    List<PositionEntity> findAllByProcessingStatus(ProcessingStatus processingStatus);

    List<PositionEntity> findByMatching1SourceTradeAgreementId(String agreementId);
}
