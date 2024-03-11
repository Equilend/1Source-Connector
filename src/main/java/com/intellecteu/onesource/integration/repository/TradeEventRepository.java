package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TradeEventRepository extends JpaRepository<TradeEventEntity, Long> {

    Optional<TradeEventEntity> findById(Long id);

    List<TradeEventEntity> findAllByProcessingStatus(ProcessingStatus status);

    @Query(nativeQuery = true,
        value = "SELECT event_id FROM trade_event WHERE resource_uri=:resourceUri AND event_type=:eventType")
    Optional<Long> findEventIdByResourceUriAndEventType(String resourceUri, String eventType);

}
