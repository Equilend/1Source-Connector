package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.EventType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpEntity;

public interface OneSourceService {

    void createContract(AgreementDto agreement, ContractProposalDto contractProposalDto, PositionDto position);

    Optional<AgreementDto> findTradeAgreement(String agreementId, EventType eventType);

    Optional<Contract> findContract(String contractId);

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
