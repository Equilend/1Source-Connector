package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.Settlement;
import com.intellecteu.onesource.integration.repository.SettlementRepository;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementServiceImpl implements SettlementService {

    private final SpireService spireService;
    private final SettlementRepository settlementRepository;
    private final EventMapper eventMapper;

    @Override
    public List<SettlementDto> getSettlementInstruction(PositionDto positionDto) {
        final Optional<PartyRole> partyRole = IntegrationUtils.extractPartyRole(positionDto.unwrapPositionType());
        return partyRole
            .map(p -> requestSettlementDetails(p, positionDto))
            .orElse(List.of());
    }

    @Override
    public SettlementDto persistSettlement(SettlementDto settlementDto) {
        final Settlement persistedSettlement = settlementRepository.save(eventMapper.toSettlementEntity(settlementDto));
        return eventMapper.toSettlementDto(persistedSettlement);
    }

    private List<SettlementDto> requestSettlementDetails(PartyRole partyRole, PositionDto positionDto) {
        log.debug("Retrieving Settlement Instruction by position from Spire as a {}", partyRole);
        return spireService.retrieveSettlementDetails(positionDto,
            positionDto.getCustomValue2(), null, partyRole);
    }
}
