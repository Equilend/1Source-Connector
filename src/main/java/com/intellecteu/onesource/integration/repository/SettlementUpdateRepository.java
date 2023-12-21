package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.SettlementInstructionUpdate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettlementUpdateRepository extends JpaRepository<SettlementInstructionUpdate, Long> {

    @Query("select s from SettlementInstructionUpdate s left join fetch s.instruction i where s.venueRefId = :venueRefId")
    List<SettlementInstructionUpdate> findByVenueRefId(@Param("venueRefId") String venueRefId);
}
