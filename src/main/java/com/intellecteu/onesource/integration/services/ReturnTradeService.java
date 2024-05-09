package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.model.backoffice.ReturnTrade;
import com.intellecteu.onesource.integration.repository.ReturnTradeRepository;
import com.intellecteu.onesource.integration.repository.entity.backoffice.ReturnTradeEntity;
import java.util.Optional;
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

    public Optional<Long> getMaxTradeId() {
        Optional<ReturnTradeEntity> lastReturnTrade = returnTradeRepository.findTopByOrderByTradeIdDesc();
        return lastReturnTrade.map(returnTrade -> returnTrade.getTradeId());
    }
}
