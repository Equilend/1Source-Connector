package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.repository.entity.backoffice.ReturnTradeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnTradeRepository extends JpaRepository<ReturnTradeEntity, Long> {

    Optional<ReturnTradeEntity> findTopByOrderByTradeIdDesc();
}
