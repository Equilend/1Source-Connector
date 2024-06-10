package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Return;
import com.intellecteu.onesource.integration.repository.ReturnTradeRepository;
import com.intellecteu.onesource.integration.repository.entity.backoffice.ReturnTradeEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReturnTradeService {

    private final ReturnTradeRepository returnTradeRepository;
    private final BackOfficeMapper backOfficeMapper;

    @Autowired
    public ReturnTradeService(ReturnTradeRepository returnTradeRepository, BackOfficeMapper backOfficeMapper) {
        this.returnTradeRepository = returnTradeRepository;
        this.backOfficeMapper = backOfficeMapper;
    }

    public ReturnTrade save(ReturnTrade returnTrade) {
        ReturnTradeEntity returnTradeEntity = returnTradeRepository.save(backOfficeMapper.toEntity(returnTrade));
        return backOfficeMapper.toModel(returnTradeEntity);
    }

    public ReturnTrade getByTradeId(Long tradeId) {
        ReturnTradeEntity returnTradeEntity = returnTradeRepository.getReferenceById(tradeId);
        return backOfficeMapper.toModel(returnTradeEntity);
    }

    public Optional<Long> getMaxTradeId() {
        Optional<ReturnTradeEntity> lastReturnTrade = returnTradeRepository.findTopByOrderByTradeIdDesc();
        return lastReturnTrade.map(returnTrade -> returnTrade.getTradeId());
    }

    public Optional<ReturnTrade> findUnmatchedReturnTrade(String contractId, LocalDate returnDate, Integer quantity,
        ProcessingStatus status) {
        List<ReturnTradeEntity> unmatchedReturnTradeEntities = returnTradeRepository.findUnmatchedReturnTradeEntity(
            contractId, status);
        return unmatchedReturnTradeEntities.stream()
            .filter(returnTradeEntity -> returnDate.equals(returnTradeEntity.getTradeOut().getTradeDate().toLocalDate())
                && quantity.equals(Integer.valueOf(returnTradeEntity.getTradeOut().getQuantity().intValue())))
            .map(backOfficeMapper::toModel).findFirst();
    }

    public Optional<ReturnTrade> findUnmatchedReturnTrade(Long tradeId, ProcessingStatus status){
        Optional<ReturnTradeEntity> unmatchedReturnTradeEntities = returnTradeRepository.findByTradeIdAndProcessingStatusAndMatching1SourceReturnIdNull(
            tradeId, status);
        return unmatchedReturnTradeEntities.map(backOfficeMapper::toModel);
    }

    public List<ReturnTrade> findReturnTrade(Long positionId, List<ProcessingStatus> processingStatuses) {
        List<ReturnTradeEntity> returnTradeEntityList = returnTradeRepository.findByRelatedPositionIdAndProcessingStatusIn(
            positionId, processingStatuses);
        return returnTradeEntityList.stream().map(backOfficeMapper::toModel).collect(Collectors.toList());
    }

    public List<ReturnTrade> findReturnTradeWithStatus(List<String> statuses) {
        List<ReturnTradeEntity> returnTradeEntityList = returnTradeRepository.findByTradeOut_StatusIn(statuses);
        return returnTradeEntityList.stream().map(backOfficeMapper::toModel).collect(Collectors.toList());
    }

    public ReturnTrade markReturnTradeAsMatched(ReturnTrade returnTrade, Return oneSourceReturn) {
        returnTrade.setMatching1SourceReturnId(oneSourceReturn.getReturnId());
        returnTrade.setLastUpdateDatetime(LocalDateTime.now());
        return save(returnTrade);
    }
}
