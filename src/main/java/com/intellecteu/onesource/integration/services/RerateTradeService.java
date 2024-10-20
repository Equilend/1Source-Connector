package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.repository.RerateTradeRepository;
import com.intellecteu.onesource.integration.repository.entity.backoffice.RerateTradeEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public RerateTrade getByTradeId(Long tradeId) {
        RerateTradeEntity rerateTradeEntity = rerateTradeRepository.getReferenceById(tradeId);
        return backOfficeMapper.toModel(rerateTradeEntity);
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

    public Optional<RerateTrade> findUnmatchedRerateTrade(String contractId, LocalDate accrualDate) {
        List<RerateTrade> rerateTradesWithRelatedContractId = rerateTradeRepository.findUnmatchedRerateTrades(
            contractId).stream().map(backOfficeMapper::toModel).collect(
            Collectors.toList());
        Optional<RerateTrade> rerateTrade = rerateTradesWithRelatedContractId.stream()
            .filter(r -> r.getTradeOut().getAccrualDate().toLocalDate()
                .equals(accrualDate))
            .findFirst();
        return rerateTrade;
    }

    public RerateTrade markRerateTradeAsMatchedWithRerate(RerateTrade rerateTrade, Rerate rerate) {
        rerateTrade.setMatchingRerateId(rerate.getRerateId());
        rerateTrade.setLastUpdateDatetime(LocalDateTime.now());
        return save(rerateTrade);
    }

    public List<RerateTrade> findReturnTrade(Long positionId, List<ProcessingStatus> processingStatuses) {
        List<RerateTradeEntity> rerateTradeEntities = rerateTradeRepository.findByRelatedPositionIdAndProcessingStatusIn(
            positionId, processingStatuses);
        return rerateTradeEntities.stream().map(backOfficeMapper::toModel).collect(Collectors.toList());
    }
}
