package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.ContractProposalDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.EventType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OneSourceService {

    void createContract(AgreementDto agreement, ContractProposalDto contractProposalDto, PositionDto position);

    AgreementDto findTradeAgreement(String agreementId, EventType eventType);

    Optional<ContractDto> findContract(String contractId);

    void updateContract(ContractDto contractDto, PositionDto positionDto);

    void approveContract(ContractDto contractDto);

    void declineContract(ContractDto contractDto);

    void cancelContract(Contract contract, String positionId);

    List<PartyDto> retrieveParties();

    List<TradeEventDto> retrieveEvents(LocalDateTime timeSTamp);
}
