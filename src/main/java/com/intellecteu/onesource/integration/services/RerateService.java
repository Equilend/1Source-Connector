package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.onesource.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.RerateStatus;
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

    public Optional<Rerate> findRerate(Long positionId, LocalDate effectiveDate, RerateStatus rerateStatus) {
        List<Rerate> rerateList = rerateRepository.findByRelatedSpirePositionIdAndStatus(
            positionId, rerateStatus).stream().map(oneSourceMapper::toModel).collect(Collectors.toList());
        Optional<Rerate> rerateOptional = rerateList.stream().filter(rerate ->
            (rerate.getRerate().getRebate().getFloating() != null && effectiveDate.equals(
                rerate.getRerate().getRebate().getFloating().getEffectiveDate()))
                || (rerate.getRerate().getRebate().getFixed() != null && effectiveDate.equals(
                rerate.getRerate().getRebate().getFixed().getEffectiveDate()))).findFirst();
        return rerateOptional;
    }

    public Rerate markRerateAsMatchedWithRerateTradeId(Rerate rerate, Long tradeId) {
        rerate.setMatchingSpireTradeId(tradeId);
        rerate.setLastUpdateDatetime(LocalDateTime.now());
        rerate.setProcessingStatus(ProcessingStatus.MATCHED_RERATE_TRADE);
        return saveRerate(rerate);
    }
}
