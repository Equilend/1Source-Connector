package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PositionRepository extends JpaRepository<PositionEntity, String> {

    List<PositionEntity> findAll();

    Optional<PositionEntity> getByPositionId(String positionId);

    @Query("select p from PositionEntity p where p.processingStatus <> 'CANCELED' and p.processingStatus <> 'SETTLED'")
    List<PositionEntity> findAllNotCanceledAndSettled();

    Optional<PositionEntity> findByVenueRefId(String venueRefId);

    List<PositionEntity> findAllByProcessingStatus(ProcessingStatus processingStatus);

    List<PositionEntity> findByMatching1SourceTradeAgreementId(String agreementId);
}
