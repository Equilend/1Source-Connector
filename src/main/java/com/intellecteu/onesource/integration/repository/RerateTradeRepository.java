package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.spire.RerateTrade;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RerateTradeRepository extends JpaRepository<RerateTrade, Integer> {

    Optional<RerateTrade> findTopByOrderByTradeIdDesc();

    List<RerateTrade> findByRelatedContractId(String contractId);

    List<RerateTrade> findByRelatedContractIdAndTradeOut_SettleDate(String contractId, LocalDateTime settleDate);

}
