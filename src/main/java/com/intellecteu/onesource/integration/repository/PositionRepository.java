package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.spire.Position;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findAll();

    @Query("select p from Position p where p.positionStatus <> 'CANCELED' and p.positionStatus <> 'SETTLED'")
    List<Position> findAllNotCanceledAndSettled();

    List<Position> findByVenueRefId(String venueRefId);
}
