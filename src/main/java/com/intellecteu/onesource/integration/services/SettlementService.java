package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.onesource.Settlement;
import com.intellecteu.onesource.integration.repository.SettlementRepository;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final OneSourceMapper oneSourceMapper;

    public List<Settlement> getSettlementByInstructionId(Long instructionId) {
        return settlementRepository.findByInstructionId(instructionId).stream().map(oneSourceMapper::toModel).collect(
            Collectors.toList());
    }

    public Settlement persistSettlement(Settlement settlement) {
        SettlementEntity settlementEntity = settlementRepository.save(oneSourceMapper.toEntity(settlement));
        return oneSourceMapper.toModel(settlementEntity);
    }

}
