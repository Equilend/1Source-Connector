package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.backoffice.RerateTrade;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.repository.RerateRepository;
import com.intellecteu.onesource.integration.repository.entity.onesource.RerateEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RerateService {

    private final RerateRepository rerateRepository;
    private final OneSourceMapper oneSourceMapper;

    @Autowired
    public RerateService(RerateRepository rerateRepository, OneSourceMapper oneSourceMapper) {
        this.rerateRepository = rerateRepository;
        this.oneSourceMapper = oneSourceMapper;
    }

    public Rerate saveRerate(Rerate rerate) {
        RerateEntity rerateEntity = rerateRepository.save(oneSourceMapper.toEntity(rerate));
        return oneSourceMapper.toModel(rerateEntity);
    }

    public Rerate getByRerateId(String rerateId){
        RerateEntity rerateEntity = rerateRepository.getReferenceById(rerateId);
        return oneSourceMapper.toModel(rerateEntity);
    }

    public Rerate mergeRerate(Rerate rerate, Rerate rerateUpdate){
        rerate.setRerate(rerateUpdate.getRerate());
        rerate.setRate(rerateUpdate.getRate());
        rerate.setRerateStatus(rerateUpdate.getRerateStatus());
        return rerate;
    }

    public Optional<Rerate> findUnmatchedRerate(String contractId, LocalDate effectiveDate) {
        List<Rerate> rerateList = rerateRepository.findByContractId(contractId).stream().map(oneSourceMapper::toModel)
            .collect(Collectors.toList());
        Optional<Rerate> rerateOptional = rerateList.stream().filter(rerate ->
            compareEffectiveDate(rerate, effectiveDate)).findFirst();
        return rerateOptional;
    }

    private Boolean compareEffectiveDate(Rerate rerate, LocalDate effectiveDate) {
        return (rerate.getRerate().getRebate().getFloating() != null && effectiveDate.equals(
            rerate.getRerate().getRebate().getFloating().getEffectiveDate()))
            || (rerate.getRerate().getRebate().getFixed() != null && effectiveDate.equals(
            rerate.getRerate().getRebate().getFixed().getEffectiveDate()));

    }

    public Rerate markRerateAsMatchedWithRerateTrade(Rerate rerate, RerateTrade rerateTrade) {
        rerate.setMatchingSpireTradeId(rerateTrade.getTradeId());
        rerate.setRelatedSpirePositionId(rerateTrade.getRelatedPositionId());
        rerate.setLastUpdateDatetime(LocalDateTime.now());
        rerate.setProcessingStatus(ProcessingStatus.MATCHED);
        return saveRerate(rerate);
    }
}
