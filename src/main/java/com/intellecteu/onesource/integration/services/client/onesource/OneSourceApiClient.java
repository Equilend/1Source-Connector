package com.intellecteu.onesource.integration.services.client.onesource;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.services.client.onesource.dto.RerateDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpEntity;

public interface OneSourceApiClient {

    void createContract(AgreementDto agreement, ContractProposalDto contractProposalDto, PositionDto position);

    Optional<AgreementDto> findTradeAgreement(String agreementUri, EventType eventType);

    Optional<Contract> retrieveContract(String contractUri);

    RerateDTO retrieveRerate(String rerateUri);

    SettlementDto retrieveSettlementInstruction(ContractDto contractDto);

    void updateContract(ContractDto contractDto, HttpEntity<?> request);

    @Deprecated(since = "1.0.4")
    void approveContract(ContractDto contractDto);

    void approveContract(ContractDto contractDto, SettlementDto settlementDto);

    void declineContract(ContractDto contractDto);

    void cancelContract(Contract contract, String positionId);

    List<PartyDto> retrieveParties();

    List<TradeEventDto> retrieveEvents(LocalDateTime timeSTamp);
}
