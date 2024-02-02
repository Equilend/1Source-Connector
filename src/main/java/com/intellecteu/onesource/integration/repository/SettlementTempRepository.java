package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.onesource.SettlementTemp;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettlementTempRepository extends JpaRepository<SettlementTemp, Long> {


    @Query("select s from SettlementTemp s left join fetch s.settlement st left join fetch st.instruction where s.contractId = :contractId")
    Set<SettlementTemp> findByContractId(@Param("contractId") String contractId);
}
