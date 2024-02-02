package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.RerateStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RerateRepository extends JpaRepository<Rerate, String> {

    List<Rerate> findByRelatedSpirePositionIdAndStatus(Long positionId, RerateStatus rerateStatus);
}
