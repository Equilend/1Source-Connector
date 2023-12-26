package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.ContractStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Query("select c from Contract c join fetch c.trade")
    List<Contract> findAll();

    @Query("select c from Contract c left join fetch c.lastEvent e left join fetch c.trade t left join fetch t.venue v left join fetch t.transactingParties prt where c.contractStatus = :status")
    List<Contract> findAllByContractStatus(ContractStatus status);

    @Query("select distinct c from Contract c left join fetch c.lastEvent e left join fetch c.trade t left join fetch t.venue v left join fetch t.collateral col left join fetch t.transactingParties prt left join fetch prt.party left join fetch t.rate rt left join fetch rt.fee f left join fetch rt.rebate rb left join fetch rb.fixed left join fetch rb.floating left join fetch t.instrument i left join fetch i.price where c.flowStatus <> 'PROCESSED'")
    List<Contract> findAllNotProcessed();

    @Query("select distinct c from Contract c left join fetch c.lastEvent e left join fetch c.trade t left join fetch t.venue v left join fetch t.collateral col left join fetch t.transactingParties prt left join fetch prt.party left join fetch t.rate rt left join fetch rt.fee f left join fetch rt.rebate rb left join fetch rb.fixed left join fetch rb.floating left join fetch t.instrument i left join fetch i.price where c.contractId = :contractId")
    List<Contract> findByContractId(String contractId);

    //TODO check if inner join will be enough in this case.
    @Query("select c from Contract c left join fetch c.lastEvent e left join fetch c.trade t left join fetch t.venue v left join fetch t.collateral col left join fetch t.transactingParties prt left join fetch prt.party left join fetch t.rate rt left join fetch rt.fee f left join fetch rt.rebate rb left join fetch rb.fixed left join fetch rb.floating left join fetch t.instrument i left join fetch i.price where v.venueRefKey = :venueRefId")
    List<Contract> findByVenueRefId(@Param("venueRefId") String venueRefId);

}
