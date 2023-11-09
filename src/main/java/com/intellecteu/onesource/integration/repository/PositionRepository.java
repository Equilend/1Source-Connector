package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.spire.Position;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findAll();

    List<Position> findByVenueRefId(String venueRefId);
}
