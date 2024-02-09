package com.intellecteu.onesource.integration.api.contracts;

import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContractRepositoryApi extends JpaRepository<ContractEntity, Long>,
    JpaSpecificationExecutor<ContractEntity> {

    Optional<ContractEntity> getByContractId(String contractId);

}
