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

    @Query("select p from PositionEntity p where p.positionId = :positionId and p.positionType.positionType = :type")
    Optional<PositionEntity> getByPositionIdAndType(Long positionId, String type);

    @Query("select p from PositionEntity p where p.matching1SourceLoanContractId is null "
        + "and (p.processingStatus = 'SUBMITTED' or p.processingStatus = 'UPDATE_SUBMITTED')"
        + "and p.positionId = :positionId")
    Optional<PositionEntity> getNotMatchedByPositionId(Long positionId);

    @Query("select p from PositionEntity p where p.processingStatus = 'UNMATCHED' or p.processingStatus = 'UPDATED'")
    List<PositionEntity> getNotMatchedForBorrower();

    @Query("select p from PositionEntity p where p.processingStatus <> 'CANCELED' and p.processingStatus <> 'SETTLED'")
    List<PositionEntity> findAllNotCanceledAndSettled();

    @Query("select p from PositionEntity p where p.customValue2 = :venueRefKey and p.processingStatus <> 'CANCELED'")
    Optional<PositionEntity> findByVenueRefKey(String venueRefKey);

    List<PositionEntity> findAllByProcessingStatus(ProcessingStatus processingStatus);

    @Query("select p from PositionEntity p where p.positionStatus.status = :positionStatus")
    List<PositionEntity> findAllByPositionStatus(String positionStatus);

    List<PositionEntity> findByMatching1SourceTradeAgreementId(String agreementId);

}
