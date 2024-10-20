package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.ContractStatus;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContractRepository extends JpaRepository<ContractEntity, Long> {

    @Query("select c from ContractEntity c join fetch c.trade")
    List<ContractEntity> findAll();

    @Query("select c from ContractEntity c join fetch c.trade t join fetch t.venues v where c.contractStatus = :status")
    List<ContractEntity> findAllByContractStatus(ContractStatus status);

    @Query("select distinct c from ContractEntity c left join fetch c.trade t left join fetch t.venues v left join fetch t.collateral col left join fetch t.transactingParties prt left join fetch prt.party left join fetch t.rate rt left join fetch rt.fee f left join fetch rt.rebate rb left join fetch rb.fixed left join fetch rb.floating left join fetch t.instrument i left join fetch i.price where c.processingStatus <> 'PROCESSED'")
    List<ContractEntity> findAllNotProcessed();

    List<ContractEntity> findAllByContractId(String contractId);

    Set<ContractEntity> findAllByProcessingStatus(ProcessingStatus processingStatus);

    Optional<ContractEntity> findByContractId(String contractId);

    Optional<ContractEntity> findByContractIdAndProcessingStatusNot(String contractId,
        ProcessingStatus processingStatus);

    //TODO check if inner join will be enough in this case.
    @Query("select c from ContractEntity c left join fetch c.trade t left join fetch t.venues v left join fetch t.collateral col left join fetch t.transactingParties prt left join fetch prt.party left join fetch t.rate rt left join fetch rt.fee f left join fetch rt.rebate rb left join fetch rb.fixed left join fetch rb.floating left join fetch t.instrument i left join fetch i.price where v.venueRefKey = :venueRefId")
    List<ContractEntity> findByVenueRefId(@Param("venueRefId") String venueRefId);

    List<ContractEntity> findByMatchingSpirePositionId(Long positionId);

}
