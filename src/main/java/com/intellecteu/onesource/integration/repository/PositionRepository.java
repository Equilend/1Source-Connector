package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.model.backoffice.spire.Position;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PositionRepository extends JpaRepository<Position, String> {

    List<Position> findAll();

    Optional<Position> getByPositionId(String positionId);

    @Query("select p from Position p where p.processingStatus <> 'CANCELED' and p.processingStatus <> 'SETTLED'")
    List<Position> findAllNotCanceledAndSettled();

    Optional<Position> findByVenueRefId(String venueRefId);

    List<Position> findAllByProcessingStatus(ProcessingStatus processingStatus);

    List<Position> findByMatching1SourceTradeAgreementId(String agreementId);
}
