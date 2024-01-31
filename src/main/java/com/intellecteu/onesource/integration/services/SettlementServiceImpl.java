package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.Settlement;
import com.intellecteu.onesource.integration.repository.SettlementRepository;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementServiceImpl implements SettlementService {

    private final BackOfficeService lenderBackOfficeService; //todo rename in next tasks as we have only one configured backoffice
    private final SettlementRepository settlementRepository;
    private final EventMapper eventMapper;
    private final CloudEventRecordService cloudEventRecordService;

    @Override
    public List<Settlement> getSettlementByInstructionId(Long instructionId) {
        return settlementRepository.findByInstructionId(instructionId);
    }

    @Override
    public Settlement persistSettlement(Settlement settlement) {
        return settlementRepository.save(settlement);
    }

}
