package com.intellecteu.onesource.integration.api.services.rerates;

import com.intellecteu.onesource.integration.repository.entity.onesource.RerateEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RerateApiRepository extends JpaRepository<RerateEntity, String> {

    @Override
    Optional<RerateEntity> findById(String id);
}
