package com.intellecteu.onesource.integration.api.cloudevents;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface CloudSystemEventRepositoryApi extends JpaRepository<CloudSystemEventEntity, String>,
    JpaSpecificationExecutor<CloudSystemEventEntity> {

    Page<CloudSystemEventEntity> findAllById(Pageable pageable, String id);

}
