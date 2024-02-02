package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.repository.SettlementRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementService {

    private final SettlementRepository settlementRepository;

    public List<Settlement> getSettlementByInstructionId(Long instructionId) {
        return settlementRepository.findByInstructionId(instructionId);
    }

    public Settlement persistSettlement(Settlement settlement) {
        return settlementRepository.save(settlement);
    }

}
