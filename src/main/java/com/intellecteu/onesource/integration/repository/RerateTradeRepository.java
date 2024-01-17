package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.spire.RerateTrade;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RerateTradeRepository extends JpaRepository<RerateTrade, Integer> {

    Optional<RerateTrade> findTopByOrderByTradeIdDesc();

}
