package com.intellecteu.onesource.integration.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.SettlementInstructionDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.PartyRole;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SpireService {

    PositionDto getTradePosition(AgreementDto agreement);

    ResponseEntity<JsonNode> requestPosition(String venueRefId);

    List<SettlementDto> retrieveSettlementDetails(PositionDto position, TradeAgreementDto trade,
        PartyRole partyRole);

    void updatePosition(ContractDto contract, String positionId);

    void updateInstruction(ContractDto contract, PositionDto position, String venueRefId,
        SettlementInstructionDto settlementInstructionDto, PartyRole partyRole);
}
