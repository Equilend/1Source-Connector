package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.repository.entity.backoffice.RerateTradeEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RerateTradeRepository extends JpaRepository<RerateTradeEntity, Integer> {

    Optional<RerateTradeEntity> findTopByOrderByTradeIdDesc();

    List<RerateTradeEntity> findByRelatedContractId(String contractId);

    List<RerateTradeEntity> findByRelatedContractIdAndTradeOut_SettleDate(String contractId, LocalDateTime settleDate);

}
