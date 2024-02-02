package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementInstructionUpdateEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettlementRepository extends JpaRepository<SettlementEntity, Long> {

    @Query("select s from SettlementInstructionUpdateEntity s left join fetch s.instruction i where s.instructionId = :instructionId")
    List<SettlementInstructionUpdateEntity> findByInstructionId(@Param("instructionId") String instructionId);

    List<SettlementEntity> findByInstructionId(Long instructionId);
}
