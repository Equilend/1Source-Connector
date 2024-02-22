package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.repository.RerateTradeRepository;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RerateTradeEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RerateTradeService {

    private final RerateTradeRepository rerateTradeRepository;
    private final BackOfficeMapper backOfficeMapper;

    @Autowired
    public RerateTradeService(RerateTradeRepository rerateTradeRepository, BackOfficeMapper backOfficeMapper) {
        this.rerateTradeRepository = rerateTradeRepository;
        this.backOfficeMapper = backOfficeMapper;
    }

    public RerateTrade save(RerateTrade rerateTrade) {
        RerateTradeEntity rerateTradeEntity = rerateTradeRepository.save(backOfficeMapper.toEntity(rerateTrade));
        return backOfficeMapper.toModel(rerateTradeEntity);
    }

    public List<RerateTrade> save(List<RerateTrade> rerateTradeList) {
        List<RerateTradeEntity> rerateTradeEntities = rerateTradeList.stream().map(backOfficeMapper::toEntity).collect(
            Collectors.toList());
        rerateTradeEntities = rerateTradeRepository.saveAll(rerateTradeEntities);
        return rerateTradeEntities.stream().map(backOfficeMapper::toModel).collect(Collectors.toList());
    }

    public Optional<Long> getMaxTradeId() {
        Optional<RerateTradeEntity> lastRerateTrade = rerateTradeRepository.findTopByOrderByTradeIdDesc();
        return lastRerateTrade.map(rerateTrade -> rerateTrade.getTradeId());
    }

    public Optional<RerateTrade> findRerateTradeByContractIdAndSettleDate(String contractId, LocalDate settleDate) {
        List<RerateTrade> rerateTradesWithRelatedContractId = rerateTradeRepository.findByRelatedContractIdAndProcessingStatus(
            contractId, ProcessingStatus.SUBMITTED).stream().map(backOfficeMapper::toModel).collect(
            Collectors.toList());
        Optional<RerateTrade> rerateTrade = rerateTradesWithRelatedContractId.stream()
            .filter(r -> r.getMatchingRerateId() == null && r.getTradeOut().getSettleDate().toLocalDate()
                .equals(settleDate))
            .findFirst();
        return rerateTrade;
    }
}
