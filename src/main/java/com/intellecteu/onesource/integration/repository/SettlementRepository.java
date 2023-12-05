package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.Settlement;
import com.intellecteu.onesource.integration.model.SettlementInstructionUpdate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    @Query("select s from SettlementInstructionUpdate s left join fetch s.instruction i left join fetch i.localMarketField l where s.instructionId = :instructionId")
    List<SettlementInstructionUpdate> findByInstructionId(@Param("instructionId") String instructionId);
}
