package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.utils.ApiUtils.createGetPositionNQuery;
import static com.intellecteu.onesource.integration.utils.ApiUtils.createListOfTuplesGetPositionWithoutTA;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.AndOr;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.mapper.PositionMapper;
import com.intellecteu.onesource.integration.model.PartyRole;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionApiService implements PositionService{

    private final PositionMapper positionMapper;
    private final SpireService spireService;
    private final OneSourceService oneSourceService;

    @Override
    public void createLoanContractWithoutTA() {
        PositionDto positionDto = getPositionWithoutTA();

        TradeAgreementDto tradeAgreementDto = buildTradeAgreementDto(positionDto);
        List<SettlementDto> settlementDtos = getSettlementDetailsWithoutTA(positionDto, tradeAgreementDto);
        createLoanContractProposalWithoutTA(positionDto, settlementDtos, tradeAgreementDto);
    }
    public PositionDto getPositionWithoutTA() {
        ResponseEntity<JsonNode> response = spireService.requestPosition(
            createGetPositionNQuery(null, AndOr.AND, null, createListOfTuplesGetPositionWithoutTA("positionID")));
        if (response.getBody() != null
            && response.getBody().get("data") != null
            && response.getBody().get("data").get("beans") != null
            && response.getBody().get("data").get("beans").get(0) != null) {
            JsonNode jsonNode = response.getBody().get("data").get("beans");
            List<JsonNode> positionNodes = new ArrayList<>();
            if (jsonNode.isArray()) {
                for (JsonNode positionNode : jsonNode) {
                    positionNodes.add(positionNode);
                }
            }
//            positionMapper.toPosition(jsonNode);
        }
        return null;
    }

    public List<SettlementDto> getSettlementDetailsWithoutTA(PositionDto positionDto,
        TradeAgreementDto tradeAgreementDto) {
        final PartyRole partyRole = tradeAgreementDto
            .getTransactingParties().get(0).getPartyRole();
        log.debug("Retrieving Settlement Instruction from Spire as a {}", partyRole);
        return spireService.retrieveSettlementDetails(positionDto, tradeAgreementDto, partyRole);
    }

    public void createLoanContractProposalWithoutTA(PositionDto positionDto, List<SettlementDto> settlementDtos,
        TradeAgreementDto tradeAgreementDto) {
        ContractProposalDto contractProposalDto = buildLoanContractProposal(settlementDtos, tradeAgreementDto);
        oneSourceService.createContract(null, contractProposalDto, positionDto);
    }

    private ContractProposalDto buildLoanContractProposal(List<SettlementDto> settlementDtos,
        TradeAgreementDto tradeAgreementDto) {
        return ContractProposalDto.builder()
            .settlement(settlementDtos)
            .trade(tradeAgreementDto)
            .build();
    }

    private TradeAgreementDto buildTradeAgreementDto(PositionDto positionDto) {
        return positionMapper.buildTradeAgreementDto(positionDto);
    }
}
