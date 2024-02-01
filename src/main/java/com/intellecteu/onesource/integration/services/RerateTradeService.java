package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.model.spire.RerateTrade;
import com.intellecteu.onesource.integration.repository.RerateTradeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RerateTradeService {

    private final RerateTradeRepository rerateTradeRepository;

    @Autowired
    public RerateTradeService(RerateTradeRepository rerateTradeRepository) {
        this.rerateTradeRepository = rerateTradeRepository;
    }

    public RerateTrade save(RerateTrade rerateTrade) {
        return rerateTradeRepository.save(rerateTrade);
    }

    public List<RerateTrade> save(List<RerateTrade> rerateTradeList) {
        return rerateTradeRepository.saveAll(rerateTradeList);
    }

    public Optional<Long> getMaxTradeId() {
        Optional<RerateTrade> lastRerateTrade = rerateTradeRepository.findTopByOrderByTradeIdDesc();
        return lastRerateTrade.map(rerateTrade -> rerateTrade.getTradeId());
    }

    public Optional<RerateTrade> findRerateTradeByContractIdAndSettleDate(String contractId, LocalDate settleDate) {
        List<RerateTrade> rerateTradesWithRelatedContractId = rerateTradeRepository.findByRelatedContractId(contractId);
        Optional<RerateTrade> rerateTrade = rerateTradesWithRelatedContractId.stream()
            .filter(r -> r.getTradeOut().getSettleDate().toLocalDate().equals(settleDate))
            .findFirst();
        return rerateTrade;
    }
}
