package com.intellecteu.onesource.integration.api.services.rerates;

import com.intellecteu.onesource.integration.repository.entity.onesource.RerateEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RerateApiRepository extends PagingAndSortingRepository<RerateEntity, String>,
    JpaSpecificationExecutor<RerateEntity> {

}
