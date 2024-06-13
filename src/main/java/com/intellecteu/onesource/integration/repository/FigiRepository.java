package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.repository.entity.toolkit.FigiEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FigiRepository extends JpaRepository<FigiEntity, String> {

    @Query("select f.figi from FigiEntity f where f.ticker = :ticker")
    List<String> findAllByTicker(String ticker);
}
