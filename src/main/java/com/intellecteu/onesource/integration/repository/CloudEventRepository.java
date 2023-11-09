package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.CloudEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudEventRepository extends JpaRepository<CloudEventEntity, String> {
}
