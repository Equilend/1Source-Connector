package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.repository.entity.onesource.CloudEventEntity;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CloudEventRepository extends JpaRepository<CloudEventEntity, String> {

    @Query(nativeQuery = true,
        value = "SELECT * FROM event_record WHERE processingstatus IS NULL")
    Set<CloudEventEntity> findAllWhereProcessingStatusIsNull();

    @Modifying
    @Query(nativeQuery = true,
        value = "UPDATE event_record SET processingstatus = :status WHERE id = :id")
    void updateProcessingStatusById(String id, String status);
}
