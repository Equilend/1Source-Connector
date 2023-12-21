package com.intellecteu.onesource.integration.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.SettlementInstructionDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.spire.InstructionDTO;
import com.intellecteu.onesource.integration.dto.spire.NQuery;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.exception.PositionRetrievementException;
import com.intellecteu.onesource.integration.dto.spire.Query;
import com.intellecteu.onesource.integration.model.PartyRole;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public interface SpireService {

    PositionDto getTradePosition(AgreementDto agreement);

    ResponseEntity<JsonNode> requestPosition(NQuery query);

    List<SettlementDto> retrieveSettlementDetails(PositionDto position, String venueRefId, TradeAgreementDto trade,
        PartyRole partyRole);

    // temporary method for to request Lender SPIRE side. Will be refactored after new flow Route creation
    ResponseEntity<SettlementDto> requestLenderSettlementDetails(PositionDto position,
        HttpEntity<Query> request) throws RestClientException;

    // temporary method for to request Borrower SPIRE side. Will be refactored after new flow Route creation
    ResponseEntity<SettlementDto> requestBorrowerSettlementDetails(PositionDto position,
        HttpEntity<Query> request) throws RestClientException;

    void updatePosition(ContractDto contract, String positionId);

    void updateInstruction(ContractDto contract, PositionDto position, String venueRefId,
        SettlementInstructionDto settlementInstructionDto, PartyRole partyRole);

    List<PositionDto> requestNewPositions(String maxPositionId) throws PositionRetrievementException;

    PositionDto requestPositionByVenueRefId(String venueRefId) throws PositionRetrievementException;

    void updateInstruction(InstructionDTO requestDto, SettlementDto settlementDto, PartyRole role);
}
