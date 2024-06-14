package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.repository.entity.backoffice.RecallSpireEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecallSpireRepository extends JpaRepository<RecallSpireEntity, Long> {

    Optional<RecallSpireEntity> findByRecallIdAndRelatedPositionId(Long spireRecallId, Long relatedPositionId);

}
