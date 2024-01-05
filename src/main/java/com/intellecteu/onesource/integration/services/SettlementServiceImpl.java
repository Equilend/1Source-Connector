package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.exception.NoRequiredPartyRoleException.NO_PARTY_ROLE_EXCEPTION;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.buildRequest;
import static com.intellecteu.onesource.integration.utils.SpireApiUtils.createCpGetInstructionsNQuery;
import static java.lang.String.format;

import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.spire.AccountDto;
import com.intellecteu.onesource.integration.dto.spire.InstructionDTO;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.dto.spire.Query;
import com.intellecteu.onesource.integration.dto.spire.SwiftbicDTO;
import com.intellecteu.onesource.integration.exception.NoRequiredPartyRoleException;
import com.intellecteu.onesource.integration.mapper.EventMapper;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.Settlement;
import com.intellecteu.onesource.integration.repository.SettlementRepository;
import com.intellecteu.onesource.integration.utils.IntegrationUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementServiceImpl implements SettlementService {

    private final SpireService spireService;
    private final SettlementRepository settlementRepository;
    private final EventMapper eventMapper;

    @Override
    public List<SettlementDto> getSettlementInstructions(PositionDto positionDto) {
        return getSettlementInstructions(positionDto.getPositionId(), positionDto.getDepoId(),
            positionDto.getSecurityId(), positionDto.getPositionTypeId(),
            positionDto.unwrapPositionType(), positionDto.getCurrencyId(), positionDto.getVenueRefId());
    }

    @Override
    public List<SettlementDto> getSettlementInstructions(String positionId, Integer accountId, Long securityId,
        Integer positionTypeId, String positionType, Integer currencyId, String venueRefId) {
        final Optional<PartyRole> partyRole = IntegrationUtils.extractPartyRole(positionType);
        return partyRole
            .map(
                p -> requestSettlementDetails(positionId, accountId, securityId, positionTypeId, currencyId, venueRefId,
                    p))
            .orElse(List.of());
    }

    public Optional<Settlement> getSettlementInstruction(String positionId, Integer accountId, Long securityId,
        Integer positionTypeId, String positionType, Integer currencyId, String venueRefId) {
        final Optional<PartyRole> partyRole = IntegrationUtils.extractPartyRole(positionType);
        if (partyRole.isPresent()) {
            List<SettlementDto> settlementDtos = requestSettlementDetails(positionId, accountId, securityId,
                positionTypeId, currencyId, venueRefId,
                partyRole.get());
            return settlementDtos.stream().findFirst()
                .map(settlementDto -> eventMapper.toSettlementEntity(settlementDto));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public ResponseEntity<SettlementDto> retrieveSettlementDetails(PositionDto positionDto,
        PartyRole partyRole, String accountId) throws RestClientException {
        final HttpEntity<Query> request = buildRequest(createCpGetInstructionsNQuery(positionDto, accountId));
        return getSettlementDetailsByRole(positionDto, request, partyRole);
    }

    private ResponseEntity<SettlementDto> getSettlementDetailsByRole(PositionDto positionDto, HttpEntity<Query> request,
        @NonNull PartyRole partyRole) throws RestClientException {
        if (partyRole == PartyRole.LENDER) {
            return spireService.requestLenderSettlementDetails(positionDto, request);
        }
        if (partyRole == PartyRole.BORROWER) {
            return spireService.requestBorrowerSettlementDetails(positionDto, request);
        }
        throw new NoRequiredPartyRoleException(format(NO_PARTY_ROLE_EXCEPTION, positionDto.getPositionId()));
    }

    @Override
    public SettlementDto persistSettlement(SettlementDto settlementDto) {
        final Settlement persistedSettlement = settlementRepository.save(eventMapper.toSettlementEntity(settlementDto));
        return eventMapper.toSettlementDto(persistedSettlement);
    }

    @Override
    public List<Settlement> getSettlementByInstructionId(Integer instructionId) {
        return settlementRepository.findByInstructionId(instructionId);
    }

    @Override
    public Settlement persistSettlement(Settlement settlement) {
        return settlementRepository.save(settlement);
    }

    @Override
    public void updateSpireInstruction(@NonNull SettlementDto spireSI, @NonNull SettlementDto contractSI,
        @NonNull PartyRole partyRole) {
        InstructionDTO requestBody = createInstructionUpdateRequestBody(contractSI);
        log.debug("Updating Spire settlement instruction for {}", partyRole);
        spireService.updateInstruction(requestBody, spireSI, partyRole);
    }

    private InstructionDTO createInstructionUpdateRequestBody(SettlementDto settlementDto) {
        try {
            final AccountDto accountDTO = new AccountDto();
            accountDTO.setDtc(
                Long.valueOf(settlementDto.getInstruction().getDtcParticipantNumber()));
            return InstructionDTO.builder()
                .agentName(settlementDto.getInstruction().getLocalAgentName())
                .agentSafe(settlementDto.getInstruction().getLocalAgentAcct())
                .accountDTO(accountDTO)
                .agentBicDTO(
                    new SwiftbicDTO(
                        settlementDto.getInstruction().getSettlementBic(),
                        settlementDto.getInstruction().getLocalAgentBic()))
                .build();
        } catch (NumberFormatException e) {
            log.warn("Parse data exception. Check the data correctness");
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }

    private List<SettlementDto> requestSettlementDetails(PartyRole partyRole, PositionDto positionDto) {
        log.debug("Retrieving Settlement Instruction by position from Spire as a {}", partyRole);
        return spireService.retrieveSettlementDetails(positionDto,
            positionDto.getCustomValue2(), null, partyRole);
    }

    private List<SettlementDto> requestSettlementDetails(String positionId, Integer accountId, Long securityId,
        Integer positionTypeId, Integer currencyId, String venueRefId, PartyRole partyRole) {
        log.debug("Retrieving Settlement Instruction by position from Spire as a {}", partyRole);
        return spireService.retrieveSettlementDetails(positionId, accountId, securityId, positionTypeId,
            currencyId,
            venueRefId, null, partyRole);
    }
}
