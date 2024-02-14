package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementTempEntity;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettlementTempRepository extends JpaRepository<SettlementTempEntity, Long> {


    @Query("select s from SettlementTempEntity s left join fetch s.settlement st left join fetch st.instruction where s.contractId = :contractId")
    Set<SettlementTempEntity> findByContractId(@Param("contractId") String contractId);
}
