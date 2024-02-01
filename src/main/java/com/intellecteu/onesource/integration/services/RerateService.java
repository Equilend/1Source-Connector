package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.model.ProcessingStatus;
import com.intellecteu.onesource.integration.model.Rerate;
import com.intellecteu.onesource.integration.model.RerateStatus;
import com.intellecteu.onesource.integration.repository.RerateRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RerateService {

    private final RerateRepository rerateRepository;

    @Autowired
    public RerateService(RerateRepository rerateRepository) {
        this.rerateRepository = rerateRepository;
    }

    public Rerate saveRerate(Rerate rerate) {
        return rerateRepository.save(rerate);
    }

    public Optional<Rerate> findRerate(Long positionId, LocalDate effectiveDate, RerateStatus rerateStatus) {
        List<Rerate> rerateList = rerateRepository.findByRelatedSpirePositionIdAndStatus(
            positionId, rerateStatus);
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
