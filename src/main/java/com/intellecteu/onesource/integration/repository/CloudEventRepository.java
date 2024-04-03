package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CloudEventRepository extends JpaRepository<CloudSystemEventEntity, String> {

    @Query(nativeQuery = true,
        value = "SELECT * FROM event_record WHERE processingstatus IS NULL")
    Set<CloudSystemEventEntity> findAllWhereProcessingStatusIsNull();

    @Modifying
    @Query(nativeQuery = true,
        value = "UPDATE event_record SET processingstatus = :status WHERE id = :id")
    void updateProcessingStatusById(String id, String status);

    @Query(nativeQuery = true, value = """
        SELECT e.id FROM event_record e \
            JOIN event_data ed ON ed.event_data_id = e.id \
            JOIN event_data_related_objects edro ON edro.event_data_id = ed.event_data_id \
        WHERE edro.related_object_id = :related \
        AND e.type = :recordType \
        AND e.relatedsubprocess = :subProcess \
        LIMIT 1""")
    Optional<String> findToolkitCloudEventId(String related, String subProcess, String recordType);


    @Query(nativeQuery = true, value = """
        SELECT e.id FROM event_record e \
        WHERE e.subject = :subject \
        AND e.type = :recordType \
        AND e.relatedsubprocess = :subProcess \
        LIMIT 1""")
    Optional<String> findToolkitCloudEventIdBySubject(String subject, String subProcess, String recordType);

    @Modifying
    @Query(value = "UPDATE CloudSystemEventEntity e SET e.time = CURRENT_TIMESTAMP WHERE e.id = :eventId")
    void updateTime(String eventId);
}
